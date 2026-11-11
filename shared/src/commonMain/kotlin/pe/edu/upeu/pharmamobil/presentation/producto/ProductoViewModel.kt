package pe.edu.upeu.pharmamobil.presentation.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.permiso.GestorPermisos
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarProductoUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.SincronizarUseCase

class ProductoViewModel(
    private val repository: ProductoRepository,
    private val registrarProductoUseCase: RegistrarProductoUseCase,
    private val sincronizarUseCase: SincronizarUseCase,
    private val compartidor: Compartidor,
    val gestorPermisos: GestorPermisos,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoUiState())
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    init {
        observarProductosLocales()
        observarResumenSync()
        sincronizarConServidor()
    }

    private fun observarProductosLocales() {
        viewModelScope.launch {
            repository.observar()
                .catch { e ->
                    _uiState.update { it.copy(fase = ProductoFase.Error("Error al cargar productos locales: ${e.message}")) }
                }
                .collectLatest { lista ->
                    val uiList = lista.map { it.toUi() }
                    _uiState.update {
                        if (uiList.isEmpty()) {
                            it.copy(fase = ProductoFase.SinProductos)
                        } else {
                            it.copy(fase = ProductoFase.ConProductos(uiList))
                        }
                    }
                }
        }
    }

    private fun observarResumenSync() {
        viewModelScope.launch {
            repository.observarResumenSync()
                .catch { }
                .collectLatest { resumen ->
                    _uiState.update { current ->
                        current.copy(
                            syncState = current.syncState.copy(
                                pendientes = resumen.pendientes,
                                fallidas = resumen.fallidas,
                                ultimaSyncMs = resumen.ultimaSyncMs,
                            ),
                        )
                    }
                }
        }
    }

    fun sincronizarConServidor() {
        viewModelScope.launch {
            _uiState.update { it.copy(syncState = it.syncState.copy(sincronizando = true)) }
            val result = sincronizarUseCase()
            _uiState.update {
                it.copy(
                    sinConexion = result.isFailure,
                    syncState = it.syncState.copy(sincronizando = false),
                )
            }
        }
    }

    fun compartirProducto(producto: ProductoUiModel) {
        val texto = "${producto.nombre} ${producto.precioFormateado} Stock: ${producto.stock}"
        compartidor.compartir(texto)
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
                stock = currentState.stock,
            )

            result.onSuccess { producto ->
                _uiState.update {
                    it.copy(
                        nombre = "",
                        precio = "",
                        stock = "",
                        mensajeExito = "Producto \"${producto.nombre}\" guardado localmente",
                        nombreError = null,
                        precioError = null,
                        stockError = null,
                    )
                }
                sincronizarConServidor()
            }.onFailure { e ->
                if (e is pe.edu.upeu.pharmamobil.domain.usecase.ValidationException) {
                    _uiState.update {
                        it.copy(
                            nombreError = e.errors["nombre"],
                            precioError = e.errors["precio"],
                            stockError = e.errors["stock"],
                            mensajeExito = null,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            mensajeExito = "Error: ${e.message}",
                            nombreError = null,
                            precioError = null,
                            stockError = null,
                        )
                    }
                }
            }
        }
    }
}
