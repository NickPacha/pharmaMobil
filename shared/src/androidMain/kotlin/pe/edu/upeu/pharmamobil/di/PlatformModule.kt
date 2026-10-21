package pe.edu.upeu.pharmamobil.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.data.local.AndroidAlmacenSeguro
import pe.edu.upeu.pharmamobil.data.local.DriverFactory
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.domain.platform.CompartidorAndroid
import pe.edu.upeu.pharmamobil.domain.repository.AlmacenSeguro

actual val platformModule = module {
    single<Compartidor> { CompartidorAndroid(androidContext()) }
    single<AlmacenSeguro> { AndroidAlmacenSeguro(androidContext()) }
    single { DriverFactory(androidContext()) }
}
