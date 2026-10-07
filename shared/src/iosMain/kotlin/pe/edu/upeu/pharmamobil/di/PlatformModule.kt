package pe.edu.upeu.pharmamobil.di

import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.domain.platform.CompartidorIos

actual val platformModule = module {
    single<Compartidor> { CompartidorIos() }
}
