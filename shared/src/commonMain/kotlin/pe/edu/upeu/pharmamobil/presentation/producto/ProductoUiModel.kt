package pe.edu.upeu.pharmamobil.presentation.producto

import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.platform.formatearSoles

data class ProductoUiModel(
    val id: Long,
    val nombre: String,
    val precio: Double,
    val precioFormateado: String,
    val stock: Int,
    val requiereReposicion: Boolean,
    val productoOriginal: Producto,
)

fun Producto.toUi(): ProductoUiModel = ProductoUiModel(
    id = id,
    nombre = nombre,
    precio = precio,
    precioFormateado = formatearSoles(precio),
    stock = stock,
    requiereReposicion = requiereReposicion,
    productoOriginal = this,
)
