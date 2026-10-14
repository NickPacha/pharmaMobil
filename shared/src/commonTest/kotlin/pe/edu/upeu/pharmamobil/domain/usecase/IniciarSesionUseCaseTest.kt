package pe.edu.upeu.pharmamobil.domain.usecase

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import pe.edu.upeu.pharmamobil.domain.model.Credenciales
import pe.edu.upeu.pharmamobil.domain.model.Sesion
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeAuthRepository : AuthRepository {
    private val _estadoSesion = MutableStateFlow<Sesion?>(null)
    override val estadoSesion: StateFlow<Sesion?> = _estadoSesion

    override suspend fun login(credenciales: Credenciales): Result<Sesion> {
        return if (credenciales.username == "admin" && credenciales.password == "Admin123") {
            val sesion = Sesion(
                token = "test_token_123",
                username = "admin",
                nombreCompleto = "Admin User",
                roles = listOf("ROLE_ADMIN"),
                permisos = listOf("CATALOGO_LEER", "CATALOGO_ESCRIBIR"),
            )
            _estadoSesion.value = sesion
            Result.success(sesion)
        } else {
            Result.failure(Exception("Credenciales inválidas"))
        }
    }

    override suspend fun obtenerSesionActual(): Sesion? = _estadoSesion.value

    override suspend fun cerrarSesion() {
        _estadoSesion.value = null
    }
}

class IniciarSesionUseCaseTest {

    @Test
    fun loginConCredencialesValidasRetornaSesionExitosa() {
        val repository = FakeAuthRepository()
        val useCase = IniciarSesionUseCase(repository)

        val credenciales = Credenciales("admin", "Admin123")
        val result = runBlocking { useCase(credenciales) }

        assertTrue(result.isSuccess)
        val sesion = result.getOrNull()
        assertEquals("admin", sesion?.username)
        assertEquals("test_token_123", sesion?.token)
    }

    @Test
    fun loginConCredencialesInvalidasRetornaFallo() {
        val repository = FakeAuthRepository()
        val useCase = IniciarSesionUseCase(repository)

        val credenciales = Credenciales("wrong", "bad_pass")
        val result = runBlocking { useCase(credenciales) }

        assertTrue(result.isFailure)
    }
}
