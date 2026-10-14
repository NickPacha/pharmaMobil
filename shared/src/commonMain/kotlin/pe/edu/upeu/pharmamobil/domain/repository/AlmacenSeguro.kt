package pe.edu.upeu.pharmamobil.domain.repository

interface AlmacenSeguro {
    fun guardar(token: String)
    fun leerToken(): String?
    fun borrar()
}
