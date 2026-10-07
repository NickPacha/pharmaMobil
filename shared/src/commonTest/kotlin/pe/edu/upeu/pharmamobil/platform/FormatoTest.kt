package pe.edu.upeu.pharmamobil.platform

import kotlin.test.Test
import kotlin.test.assertTrue

class FormatoTest {

    @Test
    fun formatearSolesDevuelveCadenaConSimboloOMonto() {
        val resultado = formatearSoles(12.50)
        assertTrue(
            resultado.contains("12") || resultado.contains("S/"),
            "El resultado '$resultado' debe contener el monto o el símbolo de moneda"
        )
    }
}
