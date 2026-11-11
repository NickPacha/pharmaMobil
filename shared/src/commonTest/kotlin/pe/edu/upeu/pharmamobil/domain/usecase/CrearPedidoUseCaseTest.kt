package pe.edu.upeu.pharmamobil.domain.usecase

import kotlinx.coroutines.runBlocking
import pe.edu.upeu.pharmamobil.data.repository.PedidoRepositorioEnMemoria
import pe.edu.upeu.pharmamobil.domain.model.DetallePedido
import pe.edu.upeu.pharmamobil.domain.model.Producto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CrearPedidoUseCaseTest {

    @Test
    fun crearPedidoValidoExitoso() = runBlocking {
        val repo = PedidoRepositorioEnMemoria()
        val useCase = CrearPedidoUseCase(repo)

        val producto = Producto(1L, "Paracetamol", 10.0, 50)
        val detalles = listOf(DetallePedido(producto, 2))

        val result = useCase("Hospital Central", detalles)

        assertTrue(result.isSuccess)
        val pedido = result.getOrThrow()
        assertEquals("Hospital Central", pedido.clienteNombre)
        assertEquals(20.0, pedido.total)
    }

    @Test
    fun rechazaPedidoSinCliente() = runBlocking {
        val repo = PedidoRepositorioEnMemoria()
        val useCase = CrearPedidoUseCase(repo)

        val producto = Producto(1L, "Paracetamol", 10.0, 50)
        val detalles = listOf(DetallePedido(producto, 2))

        val result = useCase("", detalles)

        assertTrue(result.isFailure)
    }

    @Test
    fun rechazaPedidoSinDetalles() = runBlocking {
        val repo = PedidoRepositorioEnMemoria()
        val useCase = CrearPedidoUseCase(repo)

        val result = useCase("Hospital Central", emptyList())

        assertTrue(result.isFailure)
    }
}
