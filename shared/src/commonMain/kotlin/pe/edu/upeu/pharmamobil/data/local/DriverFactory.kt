package pe.edu.upeu.pharmamobil.data.local

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun crearDriver(): SqlDriver
}
