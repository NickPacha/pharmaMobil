package pe.edu.upeu.pharmamobil.domain.usecase

import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

class SincronizarUseCase(
    private val repository: ProductoRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.sincronizar()
    }
}
