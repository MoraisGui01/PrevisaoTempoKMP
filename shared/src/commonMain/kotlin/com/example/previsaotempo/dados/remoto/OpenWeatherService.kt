package com.example.previsaotempo.dados.remoto

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val URL_PREVISAO = "https://api.openweathermap.org/data/2.5/forecast"

// Configuração comum do Ktor. Cada plataforma usa o seu motor (Android, Java ou JS),
// escolhido automaticamente pela dependência declarada no Gradle.
fun criarClienteHttp(): HttpClient = HttpClient {
    configurarCliente()
}

// Separado para que os testes usem a mesma configuração com um motor falso (MockEngine)
fun io.ktor.client.HttpClientConfig<*>.configurarCliente() {
    // Respostas 4xx e 5xx viram exceção (ClientRequestException / ServerResponseException)
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

class OpenWeatherService(
    private val cliente: HttpClient,
    private val chaveApi: String
) {

    suspend fun buscarPrevisao(cidade: String): PrevisaoResponseDto =
        cliente.get(URL_PREVISAO) {
            parameter("q", cidade)
            parameter("appid", chaveApi)
            parameter("units", "metric")
            parameter("lang", "pt_br")
        }.body()
}
