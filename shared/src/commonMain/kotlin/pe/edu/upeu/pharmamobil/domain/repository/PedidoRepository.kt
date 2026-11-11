package pe.edu.upeu.pharmamobil.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.pharmamobil.domain.model.DetallePedido
import pe.edu.upeu.pharmamobil.domain.model.EstadoPedido
import pe.edu.upeu.pharmamobil.domain.model.Pedido

interface PedidoRepository {
    fun observar(): Flow<List<Pedido>>
    suspend fun listar(): List<Pedido>
    suspend fun registrar(clienteNombre: String, detalles: List<DetallePedido>): Result<Pedido>
    suspend fun cambiarEstado(pedidoId: Long, nuevoEstado: EstadoPedido): Result<Unit>
}
