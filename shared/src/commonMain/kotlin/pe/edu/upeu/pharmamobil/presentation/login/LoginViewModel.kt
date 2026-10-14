package pe.edu.upeu.pharmamobil.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.model.Credenciales
import pe.edu.upeu.pharmamobil.domain.usecase.IniciarSesionUseCase

class LoginViewModel(
    private val iniciarSesionUseCase: IniciarSesionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun login() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Por favor ingrese usuario y contraseña") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            val credenciales = Credenciales(state.username, state.password)
            val result = iniciarSesionUseCase(credenciales)

            result.onSuccess {
                _uiState.update { it.copy(cargando = false, exito = true, error = null) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        cargando = false,
                        error = exception.message ?: "Credenciales inválidas o error de conexión",
                        exito = false,
                    )
                }
            }
        }
    }
}
