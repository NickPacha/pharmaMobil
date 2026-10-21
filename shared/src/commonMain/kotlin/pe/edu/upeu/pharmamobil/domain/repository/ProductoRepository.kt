package pe.edu.upeu.pharmamobil.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.pharmamobil.domain.model.Producto

interface ProductoRepository {
    fun observar(): Flow<List<Producto>>
    suspend fun listar(): List<Producto>
    suspend fun registrar(producto: Producto): Result<Producto>
    suspend fun sincronizar(): Result<Unit>
}
