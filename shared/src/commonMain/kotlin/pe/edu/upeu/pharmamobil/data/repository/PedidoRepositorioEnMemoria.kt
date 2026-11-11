package pe.edu.upeu.pharmamobil.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upeu.pharmamobil.domain.model.DetallePedido
import pe.edu.upeu.pharmamobil.domain.model.EstadoPedido
import pe.edu.upeu.pharmamobil.domain.model.Pedido
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.PedidoRepository

class PedidoRepositorioEnMemoria : PedidoRepository {
    private val pedidos = mutableListOf<Pedido>()
    private val _flow = MutableStateFlow<List<Pedido>>(emptyList())

    init {
        val prodDemo1 = Producto(101L, "Paracetamol 500mg", 8.50, 100)
        val prodDemo2 = Producto(102L, "Ibuprofeno 400mg", 12.00, 50)
        val p1 = Pedido(
            id = 1L,
            clienteNombre = "Clínica San Pablo",
            detalles = listOf(DetallePedido(prodDemo1, 2), DetallePedido(prodDemo2, 1)),
            estado = EstadoPedido.Pendiente,
        )
        val p2 = Pedido(
            id = 2L,
            clienteNombre = "Farmacia Boticas Perú",
            detalles = listOf(DetallePedido(prodDemo1, 5)),
            estado = EstadoPedido.Procesando,
        )
        pedidos.addAll(listOf(p1, p2))
        _flow.value = pedidos.toList()
    }

    override fun observar(): Flow<List<Pedido>> = _flow.asStateFlow()

    override suspend fun listar(): List<Pedido> = pedidos.toList()

    override suspend fun registrar(clienteNombre: String, detalles: List<DetallePedido>): Result<Pedido> {
        val nuevo = Pedido(
            id = (pedidos.size + 1).toLong(),
            clienteNombre = clienteNombre,
            detalles = detalles,
            estado = EstadoPedido.Pendiente,
        )
        pedidos.add(nuevo)
        _flow.value = pedidos.toList()
        return Result.success(nuevo)
    }

    override suspend fun cambiarEstado(pedidoId: Long, nuevoEstado: EstadoPedido): Result<Unit> {
        val index = pedidos.indexOfFirst { it.id == pedidoId }
        if (index != -1) {
            pedidos[index] = pedidos[index].copy(estado = nuevoEstado)
            _flow.value = pedidos.toList()
            return Result.success(Unit)
        }
        return Result.failure(Exception("Pedido no encontrado"))
    }
}
