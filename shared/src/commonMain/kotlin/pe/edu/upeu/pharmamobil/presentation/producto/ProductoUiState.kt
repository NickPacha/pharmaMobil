package pe.edu.upeu.pharmamobil.presentation.producto

data class ProductoUiState(
    val fase: ProductoFase = ProductoFase.Cargando,
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
