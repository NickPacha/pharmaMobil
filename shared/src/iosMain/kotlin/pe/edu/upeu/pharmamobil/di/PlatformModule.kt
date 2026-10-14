package pe.edu.upeu.pharmamobil.di

import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.data.local.IosAlmacenSeguro
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.domain.platform.CompartidorIos
import pe.edu.upeu.pharmamobil.domain.repository.AlmacenSeguro

actual val platformModule = module {
    single<Compartidor> { CompartidorIos() }
    single<AlmacenSeguro> { IosAlmacenSeguro() }
}
