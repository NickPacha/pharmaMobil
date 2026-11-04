package pe.edu.upeu.pharmamobil.permiso

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import pe.edu.upeu.pharmamobil.domain.permiso.EstadoPermiso
import pe.edu.upeu.pharmamobil.domain.permiso.GestorPermisos
import pe.edu.upeu.pharmamobil.domain.permiso.Permiso

class GestorPermisosAndroid(private val context: Context) : GestorPermisos {

    private val prefs = context.getSharedPreferences("permisos_prefs", Context.MODE_PRIVATE)

    private fun androidPermissionName(permiso: Permiso): String? {
        return when (permiso) {
            Permiso.CAMARA -> Manifest.permission.CAMERA
            Permiso.UBICACION -> Manifest.permission.ACCESS_FINE_LOCATION
            Permiso.NOTIFICACIONES -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.POST_NOTIFICATIONS
            } else {
                null
            }
        }
    }

    override suspend fun estado(permiso: Permiso): EstadoPermiso {
        val permissionName = androidPermissionName(permiso) ?: return EstadoPermiso.Concedido
        val check = ContextCompat.checkSelfPermission(context, permissionName)
        if (check == PackageManager.PERMISSION_GRANTED) {
            return EstadoPermiso.Concedido
        }
        val yaPedido = prefs.getBoolean("pedido_${permiso.name}", false)
        return if (yaPedido) {
            EstadoPermiso.DenegadoParaSiempre
        } else {
            EstadoPermiso.Denegado
        }
    }

    override suspend fun solicitar(permiso: Permiso): EstadoPermiso {
        val currentStatus = estado(permiso)
        if (currentStatus == EstadoPermiso.Concedido) return EstadoPermiso.Concedido

        prefs.edit().putBoolean("pedido_${permiso.name}", true).apply()
        return estado(permiso)
    }

    override fun abrirAjustes() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
