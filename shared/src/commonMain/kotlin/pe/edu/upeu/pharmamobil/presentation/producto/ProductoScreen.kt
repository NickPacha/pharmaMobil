package pe.edu.upeu.pharmamobil.presentation.producto

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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
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
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.presentation.components.ValidatedTextField

@Composable
fun ProductoScreen(
    viewModel: ProductoViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Gestión de Productos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        // UX-02: Exhaustividad en el Renderizado
        Box(modifier = Modifier.weight(1f)) {
            when (val fase = uiState.fase) {
                is ProductoFase.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ProductoFase.SinProductos -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay productos registrados")
                    }
                }
                is ProductoFase.ConProductos -> {
                    ListaProductosConTabs(
                        productos = fase.productos,
                    ) { viewModel.compartirProducto(it) }
                }
                is ProductoFase.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error: ${fase.mensaje}",
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = "Registro de Nuevo Producto",
            style = MaterialTheme.typography.titleMedium,
        )

        // Formulario (Persistencia del Estado del Formulario: RF-PRE-03)
        ValidatedTextField(
            value = uiState.nombre,
            onValueChange = { viewModel.onNombreChange(it) },
            label = "Nombre",
            error = uiState.nombreError,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ValidatedTextField(
                value = uiState.precio,
                onValueChange = { viewModel.onPrecioChange(it) },
                label = "Precio",
                error = uiState.precioError,
                modifier = Modifier.weight(1f),
            )

            ValidatedTextField(
                value = uiState.stock,
                onValueChange = { viewModel.onStockChange(it) },
                label = "Stock",
                error = uiState.stockError,
                modifier = Modifier.weight(1f),
            )
        }

        Button(
            onClick = { viewModel.registrar() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Registrar")
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

@Composable
fun ListaProductosConTabs(
    productos: List<ProductoUiModel>,
    onCompartir: (ProductoUiModel) -> Unit,
) {
    var tabSeleccionada by remember { mutableStateOf(0) }
    val titulosTabs = listOf("Activos", "Inactivos", "Bajo stock")

    val productosFiltrados = when (tabSeleccionada) {
        0 -> productos.filter { (it.productoOriginal.activo) && (it.stock > Producto.STOCK_MINIMO) }
        1 -> productos.filter { (!it.productoOriginal.activo) || (it.stock == 0) }
        2 -> productos.filter { it.requiereReposicion }
        else -> productos
    }

    Column {
        PrimaryTabRow(selectedTabIndex = tabSeleccionada) {
            titulosTabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabSeleccionada == index,
                    onClick = { tabSeleccionada = index },
                    text = { Text(text = title) },
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(productosFiltrados) { producto ->
                ListItem(
                    headlineContent = { Text(producto.nombre) },
                    supportingContent = {
                        Text("Precio: ${producto.precioFormateado} | Stock: ${producto.stock}")
                    },
                    trailingContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            if (producto.requiereReposicion) {
                                Text(
                                    text = "Reponer",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                            IconButton(onClick = { onCompartir(producto) }) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Compartir",
                                )
                            }
                        }
                    },
                )
                HorizontalDivider()
            }
        }
    }
}
