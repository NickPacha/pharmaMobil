package pe.edu.upeu.pharmamobil.presentation.pedido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.model.DetallePedido
import pe.edu.upeu.pharmamobil.domain.model.EstadoPedido
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.PedidoRepository
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.usecase.CrearPedidoUseCase

class PedidoViewModel(
    private val pedidoRepository: PedidoRepository,
    private val productoRepository: ProductoRepository,
    private val crearPedidoUseCase: CrearPedidoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PedidoUiState())
    val uiState: StateFlow<PedidoUiState> = _uiState.asStateFlow()

    init {
        observarPedidos()
        cargarProductosDisponibles()
    }

    private fun observarPedidos() {
        viewModelScope.launch {
            pedidoRepository.observar()
                .catch { e ->
                    _uiState.update { it.copy(fase = PedidoFase.Error("Error al cargar pedidos: ${e.message}")) }
                }
                .collectLatest { lista ->
                    val uiList = lista.map { it.toUi() }
                    _uiState.update {
                        if (uiList.isEmpty()) {
                            it.copy(fase = PedidoFase.SinPedidos)
                        } else {
                            it.copy(fase = PedidoFase.ConPedidos(uiList))
                        }
                    }
                }
        }
    }

    private fun cargarProductosDisponibles() {
        viewModelScope.launch {
            productoRepository.observar()
                .catch { }
                .collectLatest { lista ->
                    _uiState.update { current ->
                        current.copy(
                            productosDisponibles = lista,
                            productoSeleccionado = current.productoSeleccionado ?: lista.firstOrNull(),
                        )
                    }
                }
        }
    }

    fun onClienteNombreChange(nombre: String) {
        _uiState.update { it.copy(clienteNombre = nombre, clienteError = null) }
    }

    fun onProductoSeleccionadoChange(producto: Producto) {
        _uiState.update { it.copy(productoSeleccionado = producto) }
    }

    fun onCantidadChange(cantidad: String) {
        _uiState.update { it.copy(cantidadText = cantidad, detalleError = null) }
    }

    fun agregarDetalle() {
        val state = _uiState.value
        val producto = state.productoSeleccionado
        if (producto == null) {
            _uiState.update { it.copy(detalleError = "Seleccione un producto") }
            return
        }

        val cantidad = state.cantidadText.toIntOrNull()
        if (cantidad == null || cantidad <= 0) {
            _uiState.update { it.copy(detalleError = "Ingrese una cantidad válida mayor a 0") }
            return
        }

        val nuevoDetalle = DetallePedido(producto = producto, cantidad = cantidad)
        val nuevosDetalles = state.detallesFormulario + nuevoDetalle

        _uiState.update {
            it.copy(
                detallesFormulario = nuevosDetalles,
                cantidadText = "1",
                detalleError = null,
            )
        }
    }

    fun eliminarDetalle(index: Int) {
        _uiState.update { state ->
            val lista = state.detallesFormulario.toMutableList()
            if (index in lista.indices) {
                lista.removeAt(index)
            }
            state.copy(detallesFormulario = lista)
        }
    }

    fun crearPedido() {
        viewModelScope.launch {
            val state = _uiState.value
            val result = crearPedidoUseCase(
                clienteNombre = state.clienteNombre,
                detalles = state.detallesFormulario,
            )

            result.onSuccess { pedido ->
                _uiState.update {
                    it.copy(
                        clienteNombre = "",
                        detallesFormulario = emptyList(),
                        mensajeExito = "Pedido #${pedido.id} registrado correctamente para \"${pedido.clienteNombre}\"",
                        clienteError = null,
                        detalleError = null,
                    )
                }
            }.onFailure { e ->
                if (e is pe.edu.upeu.pharmamobil.domain.usecase.ValidationException) {
                    _uiState.update {
                        it.copy(
                            clienteError = e.errors["cliente"],
                            detalleError = e.errors["detalles"],
                            mensajeExito = null,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            mensajeExito = "Error al crear pedido: ${e.message}",
                        )
                    }
                }
            }
        }
    }

    fun cambiarEstadoPedido(pedidoId: Long, nuevoEstado: EstadoPedido) {
        viewModelScope.launch {
            pedidoRepository.cambiarEstado(pedidoId, nuevoEstado)
        }
    }
}
