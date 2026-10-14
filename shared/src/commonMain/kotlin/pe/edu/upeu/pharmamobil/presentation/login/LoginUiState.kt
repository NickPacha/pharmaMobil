package pe.edu.upeu.pharmamobil.presentation.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false,
)
