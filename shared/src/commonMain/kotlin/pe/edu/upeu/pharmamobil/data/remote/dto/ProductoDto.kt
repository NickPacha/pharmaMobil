package pe.edu.upeu.pharmamobil.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductoDto(
    val id: Long? = null,
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val activo: Boolean = true,
)
