package pe.edu.upeu.pharmamobil.presentation.pedido

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upeu.pharmamobil.domain.model.EstadoPedido
import pe.edu.upeu.pharmamobil.platform.formatearSoles
import pe.edu.upeu.pharmamobil.presentation.components.ValidatedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidoScreen(
    viewModel: PedidoViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Gestión de Pedidos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        // Lista de Pedidos Registrados
        Box(modifier = Modifier.weight(1f)) {
            when (val fase = uiState.fase) {
                is PedidoFase.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is PedidoFase.SinPedidos -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay pedidos registrados")
                    }
                }
                is PedidoFase.ConPedidos -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(fase.pedidos) { item ->
                            ListItem(
                                headlineContent = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Text(
                                            text = item.clienteNombre,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        Surface(
                                            color = when (item.pedidoOriginal.estado) {
                                                is EstadoPedido.Pendiente -> MaterialTheme.colorScheme.tertiaryContainer
                                                is EstadoPedido.Procesando -> MaterialTheme.colorScheme.primaryContainer
                                                is EstadoPedido.Entregado -> MaterialTheme.colorScheme.secondaryContainer
                                                is EstadoPedido.Rechazado -> MaterialTheme.colorScheme.errorContainer
                                            },
                                            shape = MaterialTheme.shapes.extraSmall,
                                        ) {
                                            Text(
                                                text = item.estadoNombre,
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            )
                                        }
                                    }
                                },
                                supportingContent = {
                                    Column {
                                        Text("Ítems: ${item.detallesText}")
                                        Text(
                                            text = "Total: ${item.totalFormateado}",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                },
                                trailingContent = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (item.pedidoOriginal.estado is EstadoPedido.Pendiente) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.cambiarEstadoPedido(item.id, EstadoPedido.Procesando)
                                                },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Procesar",
                                                )
                                            }
                                        } else if (item.pedidoOriginal.estado is EstadoPedido.Procesando) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.cambiarEstadoPedido(item.id, EstadoPedido.Entregado)
                                                },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Entregar",
                                                )
                                            }
                                        }
                                    }
                                },
                            )
                            HorizontalDivider()
                        }
                    }
                }
                is PedidoFase.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error: ${fase.mensaje}",
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        Text(
            text = "Crear Nuevo Pedido",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        ValidatedTextField(
            value = uiState.clienteNombre,
            onValueChange = { viewModel.onClienteNombreChange(it) },
            label = "Cliente / Razón Social",
            error = uiState.clienteError,
            modifier = Modifier.fillMaxWidth(),
        )

        // Selección de Producto y Cantidad
        var menuExpanded by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ExposedDropdownMenuBox(
                expanded = menuExpanded,
                onExpandedChange = { menuExpanded = !menuExpanded },
                modifier = Modifier.weight(1.5f),
            ) {
                OutlinedTextField(
                    value = uiState.productoSeleccionado?.nombre ?: "Seleccionar Producto",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Producto") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) },
                    modifier = Modifier.menuAnchor(),
                )

                ExposedDropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    uiState.productosDisponibles.forEach { producto ->
                        DropdownMenuItem(
                            text = { Text("${producto.nombre} (${formatearSoles(producto.precio)})") },
                            onClick = {
                                viewModel.onProductoSeleccionadoChange(producto)
                                menuExpanded = false
                            },
                        )
                    }
                }
            }

            ValidatedTextField(
                value = uiState.cantidadText,
                onValueChange = { viewModel.onCantidadChange(it) },
                label = "Cant.",
                error = null,
                modifier = Modifier.weight(0.7f),
            )

            OutlinedButton(
                onClick = { viewModel.agregarDetalle() },
                modifier = Modifier.weight(0.8f),
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
            }
        }

        uiState.detalleError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        // Ítems agregados al pedido actual
        if (uiState.detallesFormulario.isNotEmpty()) {
            Text(
                text = "Ítems del Pedido (${uiState.detallesFormulario.size}):",
                style = MaterialTheme.typography.labelLarge,
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                uiState.detallesFormulario.forEachIndexed { index, detalle ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${detalle.cantidad}x ${detalle.producto.nombre} = ${formatearSoles(detalle.subtotal())}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        IconButton(onClick = { viewModel.eliminarDetalle(index) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.crearPedido() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Registrar Pedido")
        }

        uiState.mensajeExito?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
