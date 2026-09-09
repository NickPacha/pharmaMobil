package pe.edu.upeu.pharmamobil.presentation.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarProductoUseCase

class ProductoViewModel(
    private val repository: ProductoRepository,
    private val registrarProductoUseCase: RegistrarProductoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoUiState())
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            _uiState.update { it.copy(fase = ProductoFase.Cargando) }
            try {
                val list = repository.listar()
                _uiState.update {
                    if (list.isEmpty()) {
                        it.copy(fase = ProductoFase.SinProductos)
                    } else {
                        it.copy(fase = ProductoFase.ConProductos(list))
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(fase = ProductoFase.Error(e.message ?: "Error desconocido")) }
            }
        }
    }

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, nombreError = null) }
    }

    fun onPrecioChange(precio: String) {
        _uiState.update { it.copy(precio = precio, precioError = null) }
    }

    fun onStockChange(stock: String) {
        _uiState.update { it.copy(stock = stock, stockError = null) }
    }

    fun registrar() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val result = registrarProductoUseCase(
                nombre = currentState.nombre,
                precio = currentState.precio,
                stock = currentState.stock
            )

            result.onSuccess { producto ->
                _uiState.update {
                    it.copy(
                        nombre = "",
                        precio = "",
                        stock = "",
                        mensajeExito = "Producto \"${producto.nombre}\" registrado correctamente",
                        nombreError = null,
                        precioError = null,
                        stockError = null
                    )
                }
                cargarProductos()
            }.onFailure { e ->
                if (e is pe.edu.upeu.pharmamobil.domain.usecase.ValidationException) {
                    _uiState.update {
                        it.copy(
                            nombreError = e.errors["nombre"],
                            precioError = e.errors["precio"],
                            stockError = e.errors["stock"],
                            mensajeExito = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            mensajeExito = "Error: ${e.message}",
                            nombreError = null,
                            precioError = null,
                            stockError = null
                        )
                    }
                }
            }
        }
    }
}
