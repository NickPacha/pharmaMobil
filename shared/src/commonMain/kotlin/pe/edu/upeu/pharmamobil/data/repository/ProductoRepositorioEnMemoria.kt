package pe.edu.upeu.pharmamobil.data.repository

import kotlinx.coroutines.delay
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import kotlin.random.Random

class ProductoRepositorioEnMemoria : ProductoRepository {
    private val productos = mutableListOf(
        Producto(id = 1L, nombre = "Paracetamol", precio = 15.50, stock = 100, activo = true),
        Producto(id = 2L, nombre = "Ibuprofeno", precio = 18.90, stock = 50, activo = true),
        Producto(id = 3L, nombre = "Amoxicilina", precio = 25.00, stock = 5, activo = true),
        Producto(id = 4L, nombre = "Loratadina", precio = 12.50, stock = 0, activo = false),
        Producto(id = 5L, nombre = "Diclofenaco", precio = 20.00, stock = 3, activo = true),
    )

    private suspend fun simulateDelay() {
        delay(Random.nextLong(300, 800))
    }

    override suspend fun registrar(producto: Producto): Producto {
        simulateDelay()
        val nuevoProducto = producto.copy(
            id = (productos.maxOfOrNull { it.id } ?: 0L) + 1L
        )
        productos.add(nuevoProducto)
        return nuevoProducto
    }

    override suspend fun listar(): List<Producto> {
        simulateDelay()
        return productos.toList()
    }
}
