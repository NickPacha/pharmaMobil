package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pe.edu.upeu.pharmamobil.domain.repository.AlmacenSeguro

fun crearHttpClient(almacenSeguro: AlmacenSeguro): HttpClient {
    return HttpClient {
        defaultRequest {
            url("http://10.0.2.2:8080/")
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                },
            )
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val token = almacenSeguro.leerToken()
                    if (!token.isNullOrBlank()) {
                        BearerTokens(token, "")
                    } else {
                        null
                    }
                }
                sendWithoutRequest { request ->
                    request.url.pathSegments.lastOrNull() == "login"
                }
            }
        }

        install(Logging) {
            level = LogLevel.HEADERS
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }
}
