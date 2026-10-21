package pe.edu.upeu.pharmamobil.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

class ProductoRepositorioEnMemoria : ProductoRepository {
    private val productos = mutableListOf<Producto>()
    private val _flow = MutableStateFlow<List<Producto>>(emptyList())

    override fun observar(): Flow<List<Producto>> = _flow.asStateFlow()

    override suspend fun listar(): List<Producto> {
        return productos.toList()
    }

    override suspend fun registrar(producto: Producto): Result<Producto> {
        val nuevo = producto.copy(id = (productos.size + 1).toLong())
        productos.add(nuevo)
        _flow.value = productos.toList()
        return Result.success(nuevo)
    }

    override suspend fun sincronizar(): Result<Unit> {
        return Result.success(Unit)
    }
}
