package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoDto

open class ProductoApi(private val httpClient: HttpClient) {

    open suspend fun listar(): List<ProductoDto> {
        return httpClient.get("api/v1/productos").body()
    }

    open suspend fun registrar(dto: ProductoDto): ProductoDto {
        return httpClient.post("api/v1/productos") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()
    }
}
