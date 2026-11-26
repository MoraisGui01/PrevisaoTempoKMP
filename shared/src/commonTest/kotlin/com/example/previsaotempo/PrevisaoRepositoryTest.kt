package com.example.previsaotempo

import com.example.previsaotempo.dados.local.PrevisaoCache
import com.example.previsaotempo.dados.remoto.OpenWeatherService
import com.example.previsaotempo.dados.remoto.configurarCliente
import com.example.previsaotempo.dominio.ErroPrevisao
import com.example.previsaotempo.dominio.Previsao
import com.example.previsaotempo.repositorio.PrevisaoRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PrevisaoRepositoryTest {

    private val respostaApi = """
        {
          "city": { "name": "Goiânia", "country": "BR" },
          "list": [
            {
              "dt_txt": "2026-11-26 18:00:00",
              "main": { "temp": 29.4, "feels_like": 31.0, "temp_min": 28.0, "temp_max": 30.1, "humidity": 48 },
              "weather": [ { "description": "nuvens dispersas", "icon": "03d" } ],
              "wind": { "speed": 3.2 }
            }
          ]
        }
    """.trimIndent()

    // Cache simples em memória, só para o teste
    private class CacheFalso : PrevisaoCache {
        val dados = mutableMapOf<String, Previsao>()
        override suspend fun salvar(chave: String, previsao: Previsao) { dados[chave] = previsao }
        override suspend fun buscar(chave: String): Previsao? = dados[chave]
    }

    private fun repositorio(cache: PrevisaoCache, resposta: suspend () -> Pair<HttpStatusCode, String>): PrevisaoRepositoryImpl {
        val motor = MockEngine {
            val (status, corpo) = resposta()
            respond(corpo, status, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val cliente = HttpClient(motor) { configurarCliente() }
        return PrevisaoRepositoryImpl(OpenWeatherService(cliente, chaveApi = "teste"), cache)
    }

    @Test
    fun buscaNaApiESalvaNoCache() = runTest {
        val cache = CacheFalso()
        val resultado = repositorio(cache) { HttpStatusCode.OK to respostaApi }.obterPrevisao("Goiânia")

        assertFalse(resultado.offline)
        assertEquals("Goiânia", resultado.previsao.cidade)
        assertEquals(48, resultado.previsao.horarios.single().umidade)
        assertTrue(cache.dados.containsKey("goiania,br"))
    }

    @Test
    fun semInternetUsaPrevisaoSalva() = runTest {
        val cache = CacheFalso()
        repositorio(cache) { HttpStatusCode.OK to respostaApi }.obterPrevisao("Goiânia")

        val resultado = repositorio(cache) { throw RuntimeException("sem rede") }.obterPrevisao("goiânia ")

        assertTrue(resultado.offline)
        assertEquals("Goiânia", resultado.previsao.cidade)
    }

    @Test
    fun semInternetESemCacheGeraErro() = runTest {
        assertFailsWith<ErroPrevisao.SemConexao> {
            repositorio(CacheFalso()) { throw RuntimeException("sem rede") }.obterPrevisao("Recife")
        }
    }

    @Test
    fun cidadeInexistenteGeraErroEspecifico() = runTest {
        assertFailsWith<ErroPrevisao.CidadeNaoEncontrada> {
            repositorio(CacheFalso()) { HttpStatusCode.NotFound to """{"cod":"404"}""" }.obterPrevisao("Xyzabc")
        }
    }

    @Test
    fun chaveInvalidaGeraErroEspecifico() = runTest {
        assertFailsWith<ErroPrevisao.ChaveInvalida> {
            repositorio(CacheFalso()) { HttpStatusCode.Unauthorized to """{"cod":401}""" }.obterPrevisao("Goiânia")
        }
    }
}
