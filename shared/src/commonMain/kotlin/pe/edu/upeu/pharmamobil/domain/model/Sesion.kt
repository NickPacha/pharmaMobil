package pe.edu.upeu.pharmamobil.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Sesion(
    val token: String,
    val tipo: String = "Bearer",
    val expiraEnSegundos: Long = 3600,
    val username: String,
    val nombreCompleto: String,
    val roles: List<String> = emptyList(),
    val permisos: List<String> = emptyList(),
)
