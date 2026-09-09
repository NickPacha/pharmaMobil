package pe.edu.upeu.pharmamobil.di

import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.data.repository.ProductoRepositorioEnMemoria
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarProductoUseCase
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoViewModel

val appModule = module {
    single<ProductoRepository> { ProductoRepositorioEnMemoria() }
    factoryOf(::RegistrarProductoUseCase)
    viewModelOf(::ProductoViewModel)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule, platformModule)
    }

// for iOS
fun initKoinIos() = initKoin {}
