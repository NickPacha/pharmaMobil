package pe.edu.upeu.pharmamobil.domain.model

data class Pedido(
    val id: Long,
    val clienteNombre: String,
    val detalles: List<DetallePedido>,
    val estado: EstadoPedido = EstadoPedido.Pendiente,
    val fecha: String = "Hoy",
) {
    val total: Double
        get() = detalles.sumOf { it.subtotal() }
}
