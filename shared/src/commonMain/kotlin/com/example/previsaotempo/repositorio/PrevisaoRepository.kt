package com.example.previsaotempo.repositorio

import com.example.previsaotempo.dados.local.PrevisaoCache
import com.example.previsaotempo.dados.remoto.OpenWeatherService
import com.example.previsaotempo.dados.remoto.paraDominio
import com.example.previsaotempo.dominio.ErroPrevisao
import com.example.previsaotempo.dominio.Previsao
import com.example.previsaotempo.util.normalizarCidade
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlin.coroutines.cancellation.CancellationException

data class ResultadoPrevisao(
    val previsao: Previsao,
    // true quando a API falhou e os dados vieram do armazenamento local
    val offline: Boolean
)

interface PrevisaoRepository {
    suspend fun obterPrevisao(cidade: String): ResultadoPrevisao
}

class PrevisaoRepositoryImpl(
    private val servico: OpenWeatherService,
    private val cache: PrevisaoCache
) : PrevisaoRepository {

    override suspend fun obterPrevisao(cidade: String): ResultadoPrevisao {
        val consulta = normalizarCidade(cidade)
        // "Goiânia" e "goiania" devem encontrar a mesma previsão salva
        val chaveCache = consulta.lowercase()

        return try {
            val previsao = servico.buscarPrevisao(consulta).paraDominio()
            cache.salvar(chaveCache, previsao)
            ResultadoPrevisao(previsao, offline = false)
        } catch (e: CancellationException) {
            throw e
        } catch (e: ClientRequestException) {
            // A API respondeu com erro: dado salvo não resolve, então avisa o usuário
            throw when (e.response.status) {
                HttpStatusCode.NotFound -> ErroPrevisao.CidadeNaoEncontrada()
                HttpStatusCode.Unauthorized -> ErroPrevisao.ChaveInvalida()
                else -> ErroPrevisao.Desconhecido()
            }
        } catch (e: Throwable) {
            // Falha de rede: usa a última previsão guardada, se existir.
            // (Na Web o Ktor sinaliza a falha com um Error, não um Exception, por isso o Throwable.)
            val salva = cache.buscar(chaveCache) ?: throw ErroPrevisao.SemConexao()
            ResultadoPrevisao(salva, offline = true)
        }
    }
}
