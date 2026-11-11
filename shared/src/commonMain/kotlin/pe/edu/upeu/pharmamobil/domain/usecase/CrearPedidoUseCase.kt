package pe.edu.upeu.pharmamobil.domain.usecase

import pe.edu.upeu.pharmamobil.domain.model.DetallePedido
import pe.edu.upeu.pharmamobil.domain.model.Pedido
import pe.edu.upeu.pharmamobil.domain.repository.PedidoRepository

class CrearPedidoUseCase(
    private val repository: PedidoRepository,
) {
    suspend operator fun invoke(
        clienteNombre: String,
        detalles: List<DetallePedido>,
    ): Result<Pedido> {
        val errors = mutableMapOf<String, String>()

        if (clienteNombre.isBlank()) {
            errors["cliente"] = "El nombre del cliente es obligatorio"
        }

        if (detalles.isEmpty()) {
            errors["detalles"] = "Debe agregar al menos un producto al pedido"
        }

        if (errors.isNotEmpty()) {
            return Result.failure(ValidationException(errors))
        }

        return repository.registrar(clienteNombre.trim(), detalles)
    }
}
