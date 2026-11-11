package pe.edu.upeu.pharmamobil.presentation.pedido

import pe.edu.upeu.pharmamobil.domain.model.DetallePedido
import pe.edu.upeu.pharmamobil.domain.model.EstadoPedido
import pe.edu.upeu.pharmamobil.domain.model.Pedido
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.platform.formatearSoles

data class PedidoUiModel(
    val id: Long,
    val clienteNombre: String,
    val totalFormateado: String,
    val estadoNombre: String,
    val cantidadItems: Int,
    val detallesText: String,
    val pedidoOriginal: Pedido,
)

fun Pedido.toUi(): PedidoUiModel = PedidoUiModel(
    id = id,
    clienteNombre = clienteNombre,
    totalFormateado = formatearSoles(total),
    estadoNombre = when (estado) {
        is EstadoPedido.Pendiente -> "Pendiente"
        is EstadoPedido.Procesando -> "Procesando"
        is EstadoPedido.Entregado -> "Entregado"
        is EstadoPedido.Rechazado -> "Rechazado"
    },
    cantidadItems = detalles.sumOf { it.cantidad },
    detallesText = detalles.joinToString(", ") { "${it.cantidad}x ${it.producto.nombre}" },
    pedidoOriginal = this,
)

data class PedidoUiState(
    val fase: PedidoFase = PedidoFase.Cargando,
    val clienteNombre: String = "",
    val productoSeleccionado: Producto? = null,
    val cantidadText: String = "1",
    val detallesFormulario: List<DetallePedido> = emptyList(),
    val clienteError: String? = null,
    val detalleError: String? = null,
    val mensajeExito: String? = null,
    val productosDisponibles: List<Producto> = emptyList(),
)

sealed interface PedidoFase {
    data object Cargando : PedidoFase
    data object SinPedidos : PedidoFase
    data class ConPedidos(val pedidos: List<PedidoUiModel>) : PedidoFase
    data class Error(val mensaje: String) : PedidoFase
}
