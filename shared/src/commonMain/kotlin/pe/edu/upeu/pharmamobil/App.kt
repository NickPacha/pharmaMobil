package pe.edu.upeu.pharmamobil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import pe.edu.upeu.pharmamobil.navigation.Screen
import pe.edu.upeu.pharmamobil.presentation.cliente.ClienteScreen
import pe.edu.upeu.pharmamobil.presentation.inicio.InicioScreen
import pe.edu.upeu.pharmamobil.presentation.pedido.PedidoScreen
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoScreen
import pe.edu.upeu.pharmamobil.theme.PharmaMobilTheme

@Composable
fun App() {
    var pantallaActual by remember {
        mutableStateOf<Screen>(Screen.Inicio)
    }

    var darkTheme by remember {
        mutableStateOf(value = false)
    }

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed,
    )

    val scope = rememberCoroutineScope()

    PharmaMobilTheme(
        darkTheme = darkTheme
    ) {

        ModalNavigationDrawer(

            drawerState = drawerState,

            drawerContent = {

                ModalDrawerSheet {

                    DrawerHeader()

                    NavigationDrawerItem(
                        label = {
                            Text("Inicio")
                        },
                        selected = pantallaActual is Screen.Inicio,
                        onClick = {

                            pantallaActual = Screen.Inicio

                            scope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Inicio"
                            )
                        }
                    )

                    NavigationDrawerItem(
                        label = {
                            Text("Productos")
                        },
                        selected = pantallaActual is Screen.Productos,
                        onClick = {

                            pantallaActual = Screen.Productos

                            scope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = "Productos"
                            )
                        }
                    )

                    NavigationDrawerItem(
                        label = {
                            Text("Clientes")
                        },
                        selected = pantallaActual is Screen.Clientes,
                        onClick = {

                            pantallaActual = Screen.Clientes

                            scope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Clientes"
                            )
                        }
                    )

                    NavigationDrawerItem(
                        label = {
                            Text("Pedidos")
                        },
                        selected = pantallaActual is Screen.Pedidos,
                        onClick = {

                            pantallaActual = Screen.Pedidos

                            scope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Pedidos"
                            )
                        }
                    )

                    Spacer(
                        modifier = Modifier.padding(8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "Modo oscuro"
                        )

                        Switch(
                            checked = darkTheme,
                            onCheckedChange = {
                                darkTheme = it
                            }
                        )
                    }
                }
            }
        ) {

            Scaffold(

                topBar = {

                    TopAppBar(

                        title = {
                            Text(
                                text = tituloPantalla(
                                    pantallaActual
                                )
                            )
                        },

                        navigationIcon = {

                            IconButton(
                                onClick = {

                                    scope.launch {

                                        drawerState.open()
                                    }
                                }
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Abrir menú"
                                )
                            }
                        }
                    )
                }

            ) { paddingValues ->

                when (pantallaActual) {

                    Screen.Inicio -> {

                        InicioScreen()
                    }

                    Screen.Productos -> {

                        Column(
                            modifier = Modifier
                                .padding(paddingValues)
                        ) {

                            ProductoScreen()
                        }
                    }

                    Screen.Clientes -> {

                        Column(
                            modifier = Modifier
                                .padding(paddingValues)
                        ) {

                            ClienteScreen()
                        }
                    }

                    Screen.Pedidos -> {
                        Column(
                            modifier = Modifier
                                .padding(paddingValues),
                        ) {
                            PedidoScreen()
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun DrawerHeader() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
    ) {

        Text(
            text = "PharmaMobil",
            style = MaterialTheme.typography.headlineSmall,
        )

        Text(
            text = "Gestión farmacéutica",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


private fun tituloPantalla(
    screen: Screen
): String {

    return when (screen) {

        Screen.Inicio ->
            "Inicio"

        Screen.Productos ->
            "Productos"

        Screen.Clientes ->
            "Clientes"

        Screen.Pedidos ->
            "Pedidos"
    }

    /*
    MaterialTheme {

        val productoRepository = remember { ProductoRepository() }

        var tabSeleccionado by rememberSaveable { mutableStateOf(0) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
        ) {

            PrimaryTabRow(selectedTabIndex = tabSeleccionado) {
                TITULOS.forEachIndexed { indice, titulo ->
                    Tab(
                        selected = tabSeleccionado == indice,
                        onClick = { tabSeleccionado = indice },
                        text = { Text(titulo) }
                    )
                }
            }

            when (tabSeleccionado) {
                0 -> ClienteScreen()
                else -> ProductoScreen(
                    onRegistrar = { productoRepository.registrar(it) }
                )
            }
        }
    }*/
}
