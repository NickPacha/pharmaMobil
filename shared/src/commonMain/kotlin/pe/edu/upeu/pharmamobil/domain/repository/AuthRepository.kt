package pe.edu.upeu.pharmamobil.domain.repository

import kotlinx.coroutines.flow.StateFlow
import pe.edu.upeu.pharmamobil.domain.model.Credenciales
import pe.edu.upeu.pharmamobil.domain.model.Sesion

interface AuthRepository {
    val estadoSesion: StateFlow<Sesion?>
    suspend fun login(credenciales: Credenciales): Result<Sesion>
    suspend fun obtenerSesionActual(): Sesion?
    suspend fun cerrarSesion()
}
