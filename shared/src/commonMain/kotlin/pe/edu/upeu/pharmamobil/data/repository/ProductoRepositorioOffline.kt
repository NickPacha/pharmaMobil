package pe.edu.upeu.pharmamobil.data.repository

import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import pe.edu.upeu.pharmamobil.data.local.ProductoLocal
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoDto
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.model.ResultadoEnvio
import pe.edu.upeu.pharmamobil.domain.model.ResumenSync
import pe.edu.upeu.pharmamobil.domain.port.Telemetria
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

class ProductoRepositorioOffline(
    private val productoLocal: ProductoLocal,
    private val productoApi: ProductoApi,
    private val telemetria: Telemetria,
) : ProductoRepository {

    private var ultimaSyncMs: Long = 0L

    override fun observar(): Flow<List<Producto>> {
        return productoLocal.observar()
    }

    override fun observarResumenSync(): Flow<ResumenSync> {
        return combine(
            productoLocal.observarCantidadPendientes(),
            productoLocal.observarCantidadFallidas(),
        ) { pendientes, fallidas ->
            ResumenSync(
                pendientes = pendientes,
                fallidas = fallidas,
                ultimaSyncMs = ultimaSyncMs,
            )
        }
    }

    override suspend fun listar(): List<Producto> {
        return productoLocal.observar().first()
    }

    override suspend fun registrar(producto: Producto): Result<Producto> {
        val localGuardado = productoLocal.guardarPendiente(producto)
        telemetria.registrarEvento("producto_registrado_offline", mapOf("nombre" to producto.nombre))
        runCatching {
            sincronizar()
        }
        return Result.success(localGuardado)
    }

    override suspend fun sincronizar(): Result<Unit> {
        return runCatching {
            // Resetear recuento de intentos para permitir reintento en sincronización manual
            productoLocal.resetearIntentos()

            // RF-SYNC-05 & RN-INT-03: Subir pendientes antes de descargar
            subirPendientesConReintento()

            // Bajada y reemplazo remoto
            val remoteDtos = productoApi.listar()
            val remotos = remoteDtos.map { dto ->
                Producto(
                    id = dto.id ?: 0L,
                    nombre = dto.nombre,
                    precio = dto.precio,
                    stock = dto.stock,
                    activo = dto.activo,
                )
            }
            productoLocal.reemplazarSincronizados(remotos)
            ultimaSyncMs = 1700000000000L
        }.onFailure { e ->
            telemetria.registrarError(e, mapOf("operacion" to "sincronizar"))
        }
    }

    private suspend fun subirPendientesConReintento() {
        val pendientes = productoLocal.pendientes()
        for (p in pendientes) {
            val dto = ProductoDto(
                nombre = p.nombre,
                precio = p.precio,
                stock = p.stock,
                activo = p.activo,
            )

            try {
                val creado = productoApi.registrar(dto)
                val confirmado = Producto(
                    id = creado.id ?: p.id,
                    nombre = creado.nombre,
                    precio = creado.precio,
                    stock = creado.stock,
                    activo = creado.activo,
                )
                productoLocal.actualizarTrasSincronizar(p.id, confirmado)
            } catch (e: Throwable) {
                val clasificado = clasificarError(e)
                when (clasificado) {
                    is ResultadoEnvio.Transitorio -> {
                        productoLocal.sumarIntento(p.id, clasificado.mensaje)
                        delay(200L)
                    }
                    is ResultadoEnvio.Permanente -> {
                        productoLocal.marcarFallido(p.id, clasificado.mensaje)
                    }
                }
            }
        }
    }

    private fun clasificarError(e: Throwable): ResultadoEnvio {
        return when (e) {
            is ResponseException -> {
                when (e.response.status) {
                    HttpStatusCode.BadRequest, HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden,
                    HttpStatusCode.NotFound, HttpStatusCode.Conflict -> {
                        ResultadoEnvio.Permanente("Error HTTP ${e.response.status.value}: ${e.message}")
                    }
                    else -> {
                        ResultadoEnvio.Transitorio("Error HTTP ${e.response.status.value}: ${e.message}")
                    }
                }
            }
            else -> {
                ResultadoEnvio.Transitorio("Fallo de red/conexión: ${e.message ?: "Sin respuesta"}")
            }
        }
    }
}
