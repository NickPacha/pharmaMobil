package pe.edu.upeu.pharmamobil.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.domain.platform.CompartidorAndroid

actual val platformModule = module {
    single<Compartidor> { CompartidorAndroid(androidContext()) }
}
