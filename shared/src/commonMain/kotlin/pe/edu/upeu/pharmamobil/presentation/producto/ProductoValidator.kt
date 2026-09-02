package pe.edu.upeu.pharmamobil.presentation.producto

object ProductoValidator {

    fun validarNombre(nombre: String): String? {
        return if (nombre.isBlank()) "El nombre es obligatorio" else null
    }

    fun validarPrecio(precio: String): String? {
        val precioValor = precio.toDoubleOrNull()
        return when {
            precio.isBlank() -> "El precio es obligatorio"
            (precioValor == null || !precioValor.isFinite()) -> "El precio debe ser un número válido"
            precioValor <= 0 -> "El precio debe ser mayor a 0"
            else -> null
        }
    }

    fun validarStock(stock: String): String? {
        val stockValor = stock.toIntOrNull()
        return when {
            stock.isBlank() -> "El stock es obligatorio"
            stockValor == null -> "El stock debe ser un número entero"
            stockValor < 0 -> "El stock no puede ser negativo"
            else -> null
        }
    }
}
