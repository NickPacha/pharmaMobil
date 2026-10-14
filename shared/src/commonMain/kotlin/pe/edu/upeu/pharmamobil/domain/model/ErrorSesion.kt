package pe.edu.upeu.pharmamobil.domain.model

sealed interface ErrorSesion {
    data object CredencialesInvalidas : ErrorSesion
    data object NoAutenticado : ErrorSesion
    data object SesionExpirada : ErrorSesion
    data object SinPermiso : ErrorSesion
    data class ErrorRed(val mensaje: String) : ErrorSesion
    data class ErrorDesconocido(val mensaje: String) : ErrorSesion
}
