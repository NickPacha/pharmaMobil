package pe.edu.upeu.pharmamobil.data

import kotlinx.coroutines.runBlocking
import pe.edu.upeu.pharmamobil.data.repository.ProductoRepositorioEnMemoria
import pe.edu.upeu.pharmamobil.domain.model.Producto
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductoRepositoryTest {

    private fun nuevoProducto(nombre: String) = Producto(
        id = 0L,
        nombre = nombre,
        precio = 12.50,
        stock = 5,
    )

    @Test
    fun asignaIdCorrelativoAlRegistrar() = runBlocking {
        val repository = ProductoRepositorioEnMemoria()

        val primero = repository.registrar(nuevoProducto("Paracetamol")).getOrThrow()
        val segundo = repository.registrar(nuevoProducto("Ibuprofeno")).getOrThrow()

        assertEquals(1L, primero.id)
        assertEquals(2L, segundo.id)
    }

    @Test
    fun listarDevuelveLosProductosRegistrados() = runBlocking {
        val repository = ProductoRepositorioEnMemoria()
        repository.registrar(nuevoProducto("Paracetamol"))
        repository.registrar(nuevoProducto("Ibuprofeno"))

        val resultado = repository.listar()

        assertEquals(2, resultado.size)
        assertEquals("Paracetamol", resultado[0].nombre)
    }
}
