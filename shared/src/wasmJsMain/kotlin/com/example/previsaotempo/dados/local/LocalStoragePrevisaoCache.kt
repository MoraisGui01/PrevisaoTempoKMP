@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.example.previsaotempo.dados.local

import com.example.previsaotempo.dominio.Previsao
import com.example.previsaotempo.dominio.PrevisaoHorario
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Acesso direto ao localStorage do navegador pela interoperabilidade Kotlin/Wasm <-> JavaScript
private fun lerDoNavegador(chave: String): String? = js("window.localStorage.getItem(chave)")

private fun gravarNoNavegador(chave: String, valor: String): Unit = js("window.localStorage.setItem(chave, valor)")

// Versão serializável dos modelos, usada só para virar texto JSON
@Serializable
private data class PrevisaoSalva(val cidade: String, val pais: String, val horarios: List<HorarioSalvo>)

@Serializable
private data class HorarioSalvo(
    val dataHora: String,
    val temperatura: Double,
    val sensacao: Double,
    val minima: Double,
    val maxima: Double,
    val umidade: Int,
    val vento: Double,
    val descricao: String,
    val icone: String
)

// Diferente da versão em memória, os dados continuam salvos mesmo depois de recarregar a página
class LocalStoragePrevisaoCache : PrevisaoCache {

    private val json = Json { ignoreUnknownKeys = true }

    private fun chaveDoNavegador(chave: String) = "previsao:$chave"

    override suspend fun salvar(chave: String, previsao: Previsao) {
        val salva = PrevisaoSalva(
            cidade = previsao.cidade,
            pais = previsao.pais,
            horarios = previsao.horarios.map {
                HorarioSalvo(it.dataHora, it.temperatura, it.sensacao, it.minima, it.maxima, it.umidade, it.vento, it.descricao, it.icone)
            }
        )
        runCatching { gravarNoNavegador(chaveDoNavegador(chave), json.encodeToString(salva)) }
    }

    override suspend fun buscar(chave: String): Previsao? {
        val texto = runCatching { lerDoNavegador(chaveDoNavegador(chave)) }.getOrNull() ?: return null
        val salva = runCatching { json.decodeFromString<PrevisaoSalva>(texto) }.getOrNull() ?: return null
        return Previsao(
            cidade = salva.cidade,
            pais = salva.pais,
            horarios = salva.horarios.map {
                PrevisaoHorario(it.dataHora, it.temperatura, it.sensacao, it.minima, it.maxima, it.umidade, it.vento, it.descricao, it.icone)
            }
        )
    }
}
