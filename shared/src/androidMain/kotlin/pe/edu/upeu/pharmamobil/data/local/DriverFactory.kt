package pe.edu.upeu.pharmamobil.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import pe.edu.upeu.pharmamobil.db.PharmaDb

actual class DriverFactory(private val context: Context) {
    actual fun crearDriver(): SqlDriver {
        return AndroidSqliteDriver(PharmaDb.Schema, context, "pharma.db")
    }
}
