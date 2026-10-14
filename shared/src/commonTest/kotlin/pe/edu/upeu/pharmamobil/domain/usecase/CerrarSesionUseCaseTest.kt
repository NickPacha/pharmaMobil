package pe.edu.upeu.pharmamobil.domain.usecase

import kotlinx.coroutines.runBlocking
import pe.edu.upeu.pharmamobil.domain.model.Credenciales
import kotlin.test.Test
import kotlin.test.assertNull

class CerrarSesionUseCaseTest {

    @Test
    fun cerrarSesionLimpiaElEstadoDeSesion() {
        val repository = FakeAuthRepository()
        val loginUseCase = IniciarSesionUseCase(repository)
        val logoutUseCase = CerrarSesionUseCase(repository)

        runBlocking {
            loginUseCase(Credenciales("admin", "Admin123"))
            logoutUseCase()
        }

        assertNull(repository.estadoSesion.value)
    }
}
