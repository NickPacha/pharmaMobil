package pe.edu.upeu.pharmamobil.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upeu.pharmamobil.db.PharmaDb
import pe.edu.upeu.pharmamobil.domain.model.Producto

class ProductoLocal(database: PharmaDb) {
    private val queries = database.productoQueries

    companion object {
        const val MAX_INTENTOS = 5L
    }

    fun observar(): Flow<List<Producto>> {
        return queries.listarTodos()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                list.map {
                    Producto(
                        id = it.id,
                        nombre = it.nombre,
                        precio = it.precio,
                        stock = it.stock.toInt(),
                    )
                }
            }
    }

    fun guardarPendiente(producto: Producto): Producto {
        val minId = queries.siguienteIdLocal().executeAsOne()
        val nuevoIdLocal = if (minId < 0) minId - 1 else -1L

        val productoConId = producto.copy(id = nuevoIdLocal)
        queries.insertar(
            id = productoConId.id,
            nombre = productoConId.nombre,
            precio = productoConId.precio,
            stock = productoConId.stock.toLong(),
            pendiente = 1L,
            intentos = 0L,
            ultimoError = null,
        )
        return productoConId
    }

    fun pendientes(maxIntentos: Long = MAX_INTENTOS): List<Producto> {
        return queries.listarPendientes(maxIntentos).executeAsList().map {
            Producto(
                id = it.id,
                nombre = it.nombre,
                precio = it.precio,
                stock = it.stock.toInt(),
            )
        }
    }

    fun observarCantidadPendientes(maxIntentos: Long = MAX_INTENTOS): Flow<Long> {
        return queries.contarPendientes(maxIntentos).asFlow().mapToOne(Dispatchers.IO)
    }

    fun observarCantidadFallidas(maxIntentos: Long = MAX_INTENTOS): Flow<Long> {
        return queries.contarFallidas(maxIntentos).asFlow().mapToOne(Dispatchers.IO)
    }

    fun sumarIntento(id: Long, errorMsg: String) {
        queries.sumarIntento(errorMsg, id)
    }

    fun marcarFallido(id: Long, errorMsg: String, maxIntentos: Long = MAX_INTENTOS) {
        queries.marcarFallido(maxIntentos, errorMsg, id)
    }

    fun reemplazarSincronizados(remotos: List<Producto>) {
        queries.transaction {
            queries.borrarSincronizados()
            remotos.forEach { producto ->
                queries.insertar(
                    id = producto.id,
                    nombre = producto.nombre,
                    precio = producto.precio,
                    stock = producto.stock.toLong(),
                    pendiente = 0L,
                    intentos = 0L,
                    ultimoError = null,
                )
            }
        }
    }

    fun actualizarTrasSincronizar(oldId: Long, confirmado: Producto) {
        queries.transaction {
            if ((oldId < 0) && (oldId != confirmado.id)) {
                queries.borrarPorId(oldId)
            }
            queries.insertar(
                id = confirmado.id,
                nombre = confirmado.nombre,
                precio = confirmado.precio,
                stock = confirmado.stock.toLong(),
                pendiente = 0L,
                intentos = 0L,
                ultimoError = null,
            )
        }
    }
}
