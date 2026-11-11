package pe.edu.upeu.pharmamobil.data.local

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import pe.edu.upeu.pharmamobil.db.PharmaDb

actual class DriverFactory(private val context: Context) {
    actual fun crearDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = PharmaDb.Schema,
            context = context,
            name = "pharma.db",
            callback = object : AndroidSqliteDriver.Callback(PharmaDb.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    try {
                        db.execSQL("ALTER TABLE producto ADD COLUMN intentos INTEGER NOT NULL DEFAULT 0")
                    } catch (_: Exception) {
                    }
                    try {
                        db.execSQL("ALTER TABLE producto ADD COLUMN ultimoError TEXT")
                    } catch (_: Exception) {
                    }
                }
            },
        )
    }
}
