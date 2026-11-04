package pe.edu.upeu.pharmamobil.permiso

import pe.edu.upeu.pharmamobil.domain.permiso.EstadoPermiso
import pe.edu.upeu.pharmamobil.domain.permiso.GestorPermisos
import pe.edu.upeu.pharmamobil.domain.permiso.Permiso
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

class GestorPermisosIos : GestorPermisos {

    override suspend fun estado(permiso: Permiso): EstadoPermiso {
        return EstadoPermiso.Concedido
    }

    override suspend fun solicitar(permiso: Permiso): EstadoPermiso {
        return EstadoPermiso.Concedido
    }

    override fun abrirAjustes() {
        val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
        if (settingsUrl != null && UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
            UIApplication.sharedApplication.openURL(settingsUrl)
        }
    }
}
