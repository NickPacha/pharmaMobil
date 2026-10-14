package pe.edu.upeu.pharmamobil.di

import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.data.remote.crearHttpClient
import pe.edu.upeu.pharmamobil.data.repository.AuthRepositoryImpl
import pe.edu.upeu.pharmamobil.data.repository.ProductoRepositorioEnMemoria
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.usecase.CerrarSesionUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.IniciarSesionUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarProductoUseCase
import pe.edu.upeu.pharmamobil.presentation.login.LoginViewModel
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoViewModel

val appModule = module {
    single { crearHttpClient(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<ProductoRepository> { ProductoRepositorioEnMemoria() }

    factoryOf(::RegistrarProductoUseCase)
    factoryOf(::IniciarSesionUseCase)
    factoryOf(::CerrarSesionUseCase)

    viewModelOf(::ProductoViewModel)
    viewModelOf(::LoginViewModel)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule, platformModule)
    }

// for iOS
fun initKoinIos() = initKoin {}
