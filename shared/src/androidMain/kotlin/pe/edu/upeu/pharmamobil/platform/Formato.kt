package pe.edu.upeu.pharmamobil.platform

import java.text.NumberFormat
import java.util.Locale

actual fun formatearSoles(valor: Double): String {
    val locale = Locale.forLanguageTag("es-PE")
    val format = NumberFormat.getCurrencyInstance(locale)
    return format.format(valor)
}
