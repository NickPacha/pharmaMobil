package pe.edu.upeu.pharmamobil.domain.model

sealed interface ResultadoEnvio {
    data class Transitorio(val mensaje: String) : ResultadoEnvio
    data class Permanente(val mensaje: String) : ResultadoEnvio
}
