package pe.edu.upeu.pharmamobil.di

import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.data.local.DriverFactory
import pe.edu.upeu.pharmamobil.data.local.ProductoLocal
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.data.remote.crearHttpClient
import pe.edu.upeu.pharmamobil.data.repository.AuthRepositoryImpl
import pe.edu.upeu.pharmamobil.data.repository.PedidoRepositorioEnMemoria
import pe.edu.upeu.pharmamobil.data.repository.ProductoRepositorioOffline
import pe.edu.upeu.pharmamobil.db.PharmaDb
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository
import pe.edu.upeu.pharmamobil.domain.repository.PedidoRepository
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.usecase.CerrarSesionUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.CrearPedidoUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.IniciarSesionUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarProductoUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.SincronizarUseCase
import pe.edu.upeu.pharmamobil.presentation.login.LoginViewModel
import pe.edu.upeu.pharmamobil.presentation.pedido.PedidoViewModel
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoViewModel

val appModule = module {
    single { crearHttpClient(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // Database & Offline Persistence
    single { PharmaDb(get<DriverFactory>().crearDriver()) }
    single { ProductoLocal(get()) }
    single { ProductoApi(get()) }
    single<ProductoRepository> { ProductoRepositorioOffline(get(), get(), get()) }
    single<PedidoRepository> { PedidoRepositorioEnMemoria() }

    factoryOf(::RegistrarProductoUseCase)
    factoryOf(::SincronizarUseCase)
    factoryOf(::IniciarSesionUseCase)
    factoryOf(::CerrarSesionUseCase)
    factoryOf(::CrearPedidoUseCase)

    viewModelOf(::ProductoViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::PedidoViewModel)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule, platformModule)
    }

// for iOS
fun initKoinIos() = initKoin {}
