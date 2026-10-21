package pe.edu.upeu.pharmamobil.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import pe.edu.upeu.pharmamobil.data.local.ProductoLocal
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoDto
import pe.edu.upeu.pharmamobil.db.PharmaDb
import pe.edu.upeu.pharmamobil.domain.model.Producto
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeProductoApi : ProductoApi(HttpClient {}) {
    var hayConexion = true
    val servidorProductos = mutableListOf<ProductoDto>()
    private var idCounter = 100L

    override suspend fun listar(): List<ProductoDto> {
        if (!hayConexion) throw Exception("Sin conexion")
        return servidorProductos.toList()
    }

    override suspend fun registrar(dto: ProductoDto): ProductoDto {
        if (!hayConexion) throw Exception("Sin conexion")
        val nuevoId = idCounter++
        val registrado = dto.copy(id = nuevoId)
        servidorProductos.add(registrado)
        return registrado
    }
}

class ProductoRepositorioOfflineTest {

    private lateinit var database: PharmaDb
    private lateinit var productoLocal: ProductoLocal
    private lateinit var fakeApi: FakeProductoApi
    private lateinit var repository: ProductoRepositorioOffline

    @BeforeTest
    fun setUp() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PharmaDb.Schema.create(driver)
        database = PharmaDb(driver)
        productoLocal = ProductoLocal(database)
        fakeApi = FakeProductoApi()
        repository = ProductoRepositorioOffline(productoLocal, fakeApi)
    }

    @Test
    fun registrarSinRedGuardaLocalmenteConIdNegativoYPendiente() = runBlocking {
        fakeApi.hayConexion = false

        val producto = Producto(0L, "Paracetamol", 10.0, 5)
        val result = repository.registrar(producto)

        assertTrue(result.isSuccess)
        val list = repository.observar().first()
        assertEquals(1, list.size)
        assertTrue(list[0].id < 0, "El ID local debe ser negativo cuando está pendiente")
        assertEquals("Paracetamol", list[0].nombre)
    }

    @Test
    fun sincronizarSubaPendientesYAsignaIdRealDelServidor() = runBlocking {
        fakeApi.hayConexion = false
        val producto = Producto(0L, "Ibuprofeno", 12.0, 10)
        repository.registrar(producto)

        fakeApi.hayConexion = true
        val syncResult = repository.sincronizar()

        assertTrue(syncResult.isSuccess)
        val list = repository.observar().first()
        assertEquals(1, list.size)
        assertTrue(list[0].id > 0, "Tras sincronizar el ID debe ser positivo asignado por el servidor")
        assertEquals("Ibuprofeno", list[0].nombre)
    }

    @Test
    fun sincronizarReemplazaBaseLocalConDatosRemotos() = runBlocking {
        fakeApi.servidorProductos.add(
            ProductoDto(id = 50L, nombre = "Amoxicilina", precio = 15.0, stock = 20)
        )

        val syncResult = repository.sincronizar()

        assertTrue(syncResult.isSuccess)
        val list = repository.observar().first()
        assertEquals(1, list.size)
        assertEquals(50L, list[0].id)
        assertEquals("Amoxicilina", list[0].nombre)
    }
}
