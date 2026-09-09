package pe.edu.upeu.pharmamobil.domain.usecase

import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

class RegistrarProductoUseCase(
    private val repository: ProductoRepository
) {
    suspend operator fun invoke(
        nombre: String,
        precio: String,
        stock: String
    ): Result<Producto> {
        val errors = mutableMapOf<String, String>()

        if (nombre.isBlank()) {
            errors["nombre"] = "El nombre es obligatorio"
        }

        val precioValor = precio.toDoubleOrNull()
        if (precio.isBlank()) {
            errors["precio"] = "El precio es obligatorio"
        } else if (precioValor == null || !precioValor.isFinite()) {
            errors["precio"] = "El precio debe ser un número válido"
        } else if (precioValor <= 0) {
            errors["precio"] = "El precio debe ser mayor a 0"
        }

        val stockValor = stock.toIntOrNull()
        if (stock.isBlank()) {
            errors["stock"] = "El stock es obligatorio"
        } else if (stockValor == null) {
            errors["stock"] = "El stock debe ser un número entero"
        } else if (stockValor < 0) {
            errors["stock"] = "El stock no puede ser negativo"
        }

        if (errors.isNotEmpty()) {
            return Result.failure(ValidationException(errors))
        }

        val producto = Producto(
            id = 0L,
            nombre = nombre.trim(),
            precio = precioValor!!,
            stock = stockValor!!,
            activo = true
        )

        return try {
            Result.success(repository.registrar(producto))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class ValidationException(val errors: Map<String, String>) : Exception("Validation failed")

