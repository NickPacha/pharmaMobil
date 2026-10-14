package pe.edu.upeu.pharmamobil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository
import pe.edu.upeu.pharmamobil.navigation.Screen
import pe.edu.upeu.pharmamobil.presentation.cliente.ClienteScreen
import pe.edu.upeu.pharmamobil.presentation.inicio.InicioScreen
import pe.edu.upeu.pharmamobil.presentation.login.LoginScreen
import pe.edu.upeu.pharmamobil.presentation.login.LoginViewModel
import pe.edu.upeu.pharmamobil.presentation.pedido.PedidoScreen
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoScreen
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoViewModel
import pe.edu.upeu.pharmamobil.theme.PharmaMobilTheme

@Composable
fun App() {
    KoinContext {
        val authRepository: AuthRepository = koinInject()
        val sesion by authRepository.estadoSesion.collectAsState()

        var darkTheme by remember { mutableStateOf(false) }

        PharmaMobilTheme(darkTheme = darkTheme) {
            // RF-UI-01: Guardia de Sesión
            if (sesion == null) {
                val loginViewModel = koinViewModel<LoginViewModel>()
                LoginScreen(viewModel = loginViewModel)
            } else {
                var pantallaActual by remember { mutableStateOf<Screen>(Screen.Inicio) }
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            DrawerHeader(
                                nombreUsuario = sesion?.nombreCompleto ?: "Usuario",
                                rol = sesion?.roles?.firstOrNull() ?: "Usuario",
                            )

                            NavigationDrawerItem(
                                label = { Text("Inicio") },
                                selected = pantallaActual is Screen.Inicio,
                                onClick = {
                                    pantallaActual = Screen.Inicio
                                    scope.launch { drawerState.close() }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Inicio",
                                    )
                                },
                            )

                            NavigationDrawerItem(
                                label = { Text("Productos") },
                                selected = pantallaActual is Screen.Productos,
                                onClick = {
                                    pantallaActual = Screen.Productos
                                    scope.launch { drawerState.close() }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Medication,
                                        contentDescription = "Productos",
                                    )
                                },
                            )

                            NavigationDrawerItem(
                                label = { Text("Clientes") },
                                selected = pantallaActual is Screen.Clientes,
                                onClick = {
                                    pantallaActual = Screen.Clientes
                                    scope.launch { drawerState.close() }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Clientes",
                                    )
                                },
                            )

                            NavigationDrawerItem(
                                label = { Text("Pedidos") },
                                selected = pantallaActual is Screen.Pedidos,
                                onClick = {
                                    pantallaActual = Screen.Pedidos
                                    scope.launch { drawerState.close() }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Pedidos",
                                    )
                                },
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            NavigationDrawerItem(
                                label = { Text("Cerrar Sesión") },
                                selected = false,
                                onClick = {
                                    scope.launch {
                                        drawerState.close()
                                        authRepository.cerrarSesion()
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Cerrar Sesión",
                                    )
                                },
                            )

                            Spacer(modifier = Modifier.padding(8.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(text = "Modo oscuro")
                                Switch(
                                    checked = darkTheme,
                                    onCheckedChange = { darkTheme = it },
                                )
                            }
                        }
                    },
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(text = tituloPantalla(pantallaActual)) },
                                navigationIcon = {
                                    IconButton(
                                        onClick = { scope.launch { drawerState.open() } },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Abrir menú",
                                        )
                                    }
                                },
                            )
                        },
                    ) { paddingValues ->
                        when (pantallaActual) {
                            Screen.Inicio -> {
                                InicioScreen()
                            }
                            Screen.Productos -> {
                                Column(modifier = Modifier.padding(paddingValues)) {
                                    val viewModel = koinViewModel<ProductoViewModel>()
                                    val puedeEscribir = sesion?.permisos?.contains("CATALOGO_ESCRIBIR") ?: true
                                    ProductoScreen(
                                        viewModel = viewModel,
                                        puedeEscribir = puedeEscribir,
                                    )
                                }
                            }
                            Screen.Clientes -> {
                                Column(modifier = Modifier.padding(paddingValues)) {
                                    ClienteScreen()
                                }
                            }
                            Screen.Pedidos -> {
                                Column(modifier = Modifier.padding(paddingValues)) {
                                    PedidoScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerHeader(
    nombreUsuario: String,
    rol: String,
) {
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
            text = nombreUsuario,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = rol,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun tituloPantalla(screen: Screen): String {
    return when (screen) {
        Screen.Inicio -> "Inicio"
        Screen.Productos -> "Productos"
        Screen.Clientes -> "Clientes"
        Screen.Pedidos -> "Pedidos"
    }
}
