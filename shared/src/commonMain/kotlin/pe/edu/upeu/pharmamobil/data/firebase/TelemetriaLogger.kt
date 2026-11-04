package pe.edu.upeu.pharmamobil.data.firebase

import pe.edu.upeu.pharmamobil.domain.port.Telemetria

class TelemetriaLogger : Telemetria {
    override fun registrarError(e: Throwable, contexto: Map<String, String>) {
        println("[Telemetria Error] ${e.message} | Contexto: $contexto")
    }

    override fun registrarEvento(nombre: String, datos: Map<String, String>) {
        println("[Telemetria Evento] $nombre | Datos: $datos")
    }
}
