package pe.edu.upeu.pharmamobil.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import pe.edu.upeu.pharmamobil.domain.repository.AlmacenSeguro

class AndroidAlmacenSeguro(context: Context) : AlmacenSeguro {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val prefs = EncryptedSharedPreferences.create(
        "secure_session_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override fun guardar(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    override fun leerToken(): String? {
        return prefs.getString("auth_token", null)
    }

    override fun borrar() {
        prefs.edit().remove("auth_token").apply()
    }
}
