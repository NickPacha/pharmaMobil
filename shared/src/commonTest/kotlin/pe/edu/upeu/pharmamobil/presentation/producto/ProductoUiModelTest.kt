package pe.edu.upeu.pharmamobil.presentation.producto

import pe.edu.upeu.pharmamobil.domain.model.Producto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductoUiModelTest {

    @Test
    fun toUiMapeaCorrectamenteUnProducto() {
        val producto = Producto(
            id = 1L,
            nombre = "Paracetamol",
            precio = 15.00,
            stock = 10,
            activo = true
        )

        val uiModel = producto.toUi()

        assertEquals(1L, uiModel.id)
        assertEquals("Paracetamol", uiModel.nombre)
        assertEquals(15.00, uiModel.precio)
        assertEquals(10, uiModel.stock)
        assertTrue(uiModel.precioFormateado.isNotEmpty())
    }
}
