package pe.edu.upeu.pharmamobil.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import pe.edu.upeu.pharmamobil.db.PharmaDb

actual class DriverFactory {
    actual fun crearDriver(): SqlDriver {
        return NativeSqliteDriver(PharmaDb.Schema, "pharma.db")
    }
}
