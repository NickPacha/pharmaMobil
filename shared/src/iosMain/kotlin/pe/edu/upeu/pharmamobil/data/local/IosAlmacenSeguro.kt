package pe.edu.upeu.pharmamobil.data.local

import pe.edu.upeu.pharmamobil.domain.repository.AlmacenSeguro
import platform.Foundation.NSUserDefaults

class IosAlmacenSeguro : AlmacenSeguro {
    private val key = "secure_auth_token"

    override fun guardar(token: String) {
        NSUserDefaults.standardUserDefaults.setObject(token, key)
    }

    override fun leerToken(): String? {
        return NSUserDefaults.standardUserDefaults.stringForKey(key)
    }

    override fun borrar() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(key)
    }
}
