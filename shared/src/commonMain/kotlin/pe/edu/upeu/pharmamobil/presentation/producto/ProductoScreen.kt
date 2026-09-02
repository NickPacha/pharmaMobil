package pe.edu.upeu.pharmamobil.presentation.producto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.presentation.components.ValidatedTextField

@Composable
fun ProductoScreen(
    onRegistrar: (Producto) -> Unit = {},
) {

    var nombre by remember {
        mutableStateOf("")
    }

    var precio by remember {
        mutableStateOf("")
    }

    var stock by remember {
        mutableStateOf("")
    }

    var nombreError by remember {
        mutableStateOf<String?>(null)
    }

    var precioError by remember {
        mutableStateOf<String?>(null)
    }

    var stockError by remember {
        mutableStateOf<String?>(null)
    }

    var mensajeExito by remember {
        mutableStateOf<String?>(null)
    }

    // Reto 02: Mock Data
    val productos = remember {
        mutableStateListOf(
            Producto(id = 1L, nombre = "Paracetamol", precio = 15.50, stock = 100, activo = true),
            Producto(id = 2L, nombre = "Ibuprofeno", precio = 18.90, stock = 50, activo = true),
            Producto(id = 3L, nombre = "Amoxicilina", precio = 25.00, stock = 5, activo = true),
            Producto(id = 4L, nombre = "Loratadina", precio = 12.50, stock = 0, activo = false),
            Producto(id = 5L, nombre = "Diclofenaco", precio = 20.00, stock = 3, activo = true),
        )
    }

    // Reto 02: Control de Tabs
    var tabSeleccionada by remember {
        mutableStateOf(0)
    }

    val titulosTabs = listOf("Activos", "Inactivos", "Bajo stock")

    // Reto 02: Filtrado dinámico
    val productosFiltrados = when (tabSeleccionada) {
        0 -> productos.filter { (it.activo && it.stock > 5) }
        1 -> productos.filter { (!it.activo || it.stock == 0) }
        2 -> productos.filter { (it.activo && it.stock > 0 && it.stock <= 5) }
        else -> productos
    }

    fun validar(): Producto? {
        nombreError = ProductoValidator.validarNombre(nombre)
        precioError = ProductoValidator.validarPrecio(precio)
        stockError = ProductoValidator.validarStock(stock)

        if ((nombreError != null || precioError != null || stockError != null)) return null

        return Producto(
            id = (productos.size + 1).toLong(),
            nombre = nombre.trim(),
            precio = precio.toDouble(),
            stock = stock.toInt(),
            activo = true, // Por defecto activo al registrar
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        Text(
            text = "Inventario de Productos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        // Reto 02: Implementación de Tabs
        PrimaryTabRow(selectedTabIndex = tabSeleccionada) {
            titulosTabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabSeleccionada == index,
                    onClick = { tabSeleccionada = index },
                    text = {
                        Text(text = title)
                    },
                )
            }
        }

        // Lista de productos filtrados
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            items(productosFiltrados) { producto ->
                ListItem(
                    headlineContent = {
                        Text(producto.nombre)
                    },
                    supportingContent = {
                        Text("Precio: S/ ${producto.precio} | Stock: ${producto.stock}")
                    },
                    trailingContent = {
                        if (producto.stock <= 5) {
                            Text(
                                text = "Bajo stock",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    },
                )
                HorizontalDivider()
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = "Registro de Nuevo Producto",
            style = MaterialTheme.typography.titleMedium,
        )

        ValidatedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = "Nombre",
            error = nombreError,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ValidatedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = "Precio",
                error = precioError,
                modifier = Modifier.weight(1f),
            )

            ValidatedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = "Stock",
                error = stockError,
                modifier = Modifier.weight(1f),
            )
        }

        Button(
            onClick = {
                mensajeExito = null
                val producto = validar()
                if (producto != null) {
                    productos.add(producto)
                    onRegistrar(producto)
                    mensajeExito = "Producto \"${producto.nombre}\" registrado correctamente"
                    nombre = ""
                    precio = ""
                    stock = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Registrar")
        }

        mensajeExito?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
