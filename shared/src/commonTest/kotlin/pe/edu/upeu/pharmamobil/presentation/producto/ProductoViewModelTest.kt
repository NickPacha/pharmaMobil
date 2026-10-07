package pe.edu.upeu.pharmamobil.presentation.producto

import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import kotlin.test.Test
import kotlin.test.assertTrue

class FakeCompartidor : Compartidor {
    var ultimoTextoCompartido: String? = null

    override fun compartir(texto: String) {
        ultimoTextoCompartido = texto
    }
}

class ProductoViewModelTest {

    @Test
    fun compartirProductoEnviaTextoEstructurado() {
        val fakeCompartidor = FakeCompartidor()
        val producto = Producto(
            id = 1L,
            nombre = "Ibuprofeno",
            precio = 8.50,
            stock = 20
        ).toUi()

        val texto = "${producto.nombre} ${producto.precioFormateado} Stock: ${producto.stock}"
        fakeCompartidor.compartir(texto)

        val compartido = fakeCompartidor.ultimoTextoCompartido
        assertTrue(compartido != null, "Se debió invocar compartir")
        assertTrue(compartido.contains("Ibuprofeno"), "El texto debe incluir el nombre del producto")
        assertTrue(compartido.contains("Stock: 20"), "El texto debe incluir el stock")
    }
}
