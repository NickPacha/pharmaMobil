package pe.edu.upeu.pharmamobil.domain.permiso

sealed interface EstadoPermiso {
    data object Concedido : EstadoPermiso
    data object Denegado : EstadoPermiso
    data object DenegadoParaSiempre : EstadoPermiso
}
