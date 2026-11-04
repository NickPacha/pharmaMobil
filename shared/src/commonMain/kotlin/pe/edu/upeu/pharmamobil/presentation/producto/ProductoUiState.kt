package pe.edu.upeu.pharmamobil.presentation.producto

import pe.edu.upeu.pharmamobil.presentation.sincronizacion.SincronizacionUiState

data class ProductoUiState(
    val fase: ProductoFase = ProductoFase.Cargando,
    val sinConexion: Boolean = false,
    val syncState: SincronizacionUiState = SincronizacionUiState(),
    val nombre: String = "",
    val precio: String = "",
    val stock: String = "",
    val nombreError: String? = null,
    val precioError: String? = null,
    val stockError: String? = null,
    val mensajeExito: String? = null,
)

sealed interface ProductoFase {
    data object Cargando : ProductoFase
    data object SinProductos : ProductoFase
    data class ConProductos(val productos: List<ProductoUiModel>) : ProductoFase
    data class Error(val mensaje: String) : ProductoFase
}
