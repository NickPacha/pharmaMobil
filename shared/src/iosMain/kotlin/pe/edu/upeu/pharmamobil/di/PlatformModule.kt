package pe.edu.upeu.pharmamobil.di

import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.data.firebase.TelemetriaLogger
import pe.edu.upeu.pharmamobil.data.local.DriverFactory
import pe.edu.upeu.pharmamobil.data.local.IosAlmacenSeguro
import pe.edu.upeu.pharmamobil.domain.permiso.GestorPermisos
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.domain.platform.CompartidorIos
import pe.edu.upeu.pharmamobil.domain.port.Telemetria
import pe.edu.upeu.pharmamobil.domain.repository.AlmacenSeguro
import pe.edu.upeu.pharmamobil.permiso.GestorPermisosIos

actual val platformModule = module {
    single<Compartidor> { CompartidorIos() }
    single<AlmacenSeguro> { IosAlmacenSeguro() }
    single { DriverFactory() }
    single<GestorPermisos> { GestorPermisosIos() }
    single<Telemetria> { TelemetriaLogger() }
}
