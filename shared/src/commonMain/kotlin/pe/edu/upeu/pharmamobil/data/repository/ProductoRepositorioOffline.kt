package pe.edu.upeu.pharmamobil.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import pe.edu.upeu.pharmamobil.data.local.ProductoLocal
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoDto
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

class ProductoRepositorioOffline(
    private val productoLocal: ProductoLocal,
    private val productoApi: ProductoApi,
) : ProductoRepository {

    override fun observar(): Flow<List<Producto>> {
        return productoLocal.observar()
    }

    override suspend fun listar(): List<Producto> {
        return productoLocal.observar().first()
    }

    override suspend fun registrar(producto: Producto): Result<Producto> {
        // RF-PERS-02: Guardado local inmediato con pendiente = 1 e ID negativo
        val localGuardado = productoLocal.guardarPendiente(producto)

        // Intento de sincronización inmediata si hay red
        runCatching {
            subirPendientes()
        }

        return Result.success(localGuardado)
    }

    override suspend fun sincronizar(): Result<Unit> {
        return runCatching {
            // RF-SINC-01 / RN-OFF-01: Fase 1 - Subida de pendientes obligatoria
            subirPendientes()

            // RF-SINC-01: Fase 2 - Bajada y reemplazo remoto
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
        }
    }

    private suspend fun subirPendientes() {
        val pendientes = productoLocal.pendientes()
        for (p in pendientes) {
            val dto = ProductoDto(
                nombre = p.nombre,
                precio = p.precio,
                stock = p.stock,
                activo = p.activo,
            )
            val creado = productoApi.registrar(dto)
            val confirmado = Producto(
                id = creado.id ?: p.id,
                nombre = creado.nombre,
                precio = creado.precio,
                stock = creado.stock,
                activo = creado.activo,
            )
            productoLocal.actualizarTrasSincronizar(p.id, confirmado)
        }
    }
}
