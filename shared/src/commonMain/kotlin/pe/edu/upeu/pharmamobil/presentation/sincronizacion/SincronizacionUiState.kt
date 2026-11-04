package pe.edu.upeu.pharmamobil.presentation.sincronizacion

data class SincronizacionUiState(
    val pendientes: Long = 0,
    val fallidas: Long = 0,
    val ultimaSyncMs: Long = 0,
    val sincronizando: Boolean = false,
)
