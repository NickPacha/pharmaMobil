package pe.edu.upeu.pharmamobil.domain.platform

import android.content.Context
import android.content.Intent

class CompartidorAndroid(private val context: Context) : Compartidor {
    override fun compartir(texto: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, texto)
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooserIntent = Intent.createChooser(sendIntent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooserIntent)
    }
}
