package pe.edu.upeu.pharmamobil.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Credenciales(
    val username: String,
    val password: String,
)
