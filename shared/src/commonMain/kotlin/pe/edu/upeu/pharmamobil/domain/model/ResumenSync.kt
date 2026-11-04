package pe.edu.upeu.pharmamobil.domain.model

data class ResumenSync(
    val pendientes: Long = 0,
    val fallidas: Long = 0,
    val ultimaSyncMs: Long = 0,
)
