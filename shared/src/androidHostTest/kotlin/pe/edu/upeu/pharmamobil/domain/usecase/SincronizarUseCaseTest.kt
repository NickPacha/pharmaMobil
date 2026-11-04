package pe.edu.upeu.pharmamobil.domain.usecase

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import pe.edu.upeu.pharmamobil.data.firebase.TelemetriaLogger
import pe.edu.upeu.pharmamobil.data.local.ProductoLocal
import pe.edu.upeu.pharmamobil.data.repository.FakeProductoApi
import pe.edu.upeu.pharmamobil.data.repository.ProductoRepositorioOffline
import pe.edu.upeu.pharmamobil.db.PharmaDb
import pe.edu.upeu.pharmamobil.domain.model.Producto
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SincronizarUseCaseTest {

    private lateinit var database: PharmaDb
    private lateinit var productoLocal: ProductoLocal
    private lateinit var fakeApi: FakeProductoApi
    private lateinit var repository: ProductoRepositorioOffline
    private lateinit var useCase: SincronizarUseCase

    @BeforeTest
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PharmaDb.Schema.create(driver)
        database = PharmaDb(driver)
        productoLocal = ProductoLocal(database)
        fakeApi = FakeProductoApi()
        repository = ProductoRepositorioOffline(productoLocal, fakeApi, TelemetriaLogger())
        useCase = SincronizarUseCase(repository)
    }

    @Test
    fun reintentoTransitorioIncrementaIntentosYSeMantienePendiente() = runBlocking {
        fakeApi.hayConexion = false
        val producto = Producto(0L, "Vitamina C", 5.0, 10)
        repository.registrar(producto)

        useCase()

        val pendientesCount = productoLocal.observarCantidadPendientes().first()
        val fallidasCount = productoLocal.observarCantidadFallidas().first()
        assertEquals(1L, pendientesCount, "Sigue siendo pendiente al no superar MAX_INTENTOS")
        assertEquals(0L, fallidasCount)
    }
}
