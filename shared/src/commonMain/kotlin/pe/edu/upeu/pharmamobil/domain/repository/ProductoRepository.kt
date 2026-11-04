package pe.edu.upeu.pharmamobil.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.model.ResumenSync

interface ProductoRepository {
    fun observar(): Flow<List<Producto>>
    fun observarResumenSync(): Flow<ResumenSync>
    suspend fun listar(): List<Producto>
    suspend fun registrar(producto: Producto): Result<Producto>
    suspend fun sincronizar(): Result<Unit>
}
