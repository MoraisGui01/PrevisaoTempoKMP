package com.example.previsaotempo.apresentacao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.previsaotempo.dominio.ErroPrevisao
import com.example.previsaotempo.repositorio.PrevisaoRepository
import com.example.previsaotempo.util.agruparPorDia
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

const val CIDADE_INICIAL = "Goiânia"

class PrevisaoViewModel(
    private val repositorio: PrevisaoRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(PrevisaoUiState(cidade = CIDADE_INICIAL))
    val estado: StateFlow<PrevisaoUiState> = _estado.asStateFlow()

    // Guarda a busca em andamento para cancelar se o usuário pesquisar de novo
    private var buscaAtual: Job? = null

    init {
        buscar()
    }

    fun aoMudarCidade(texto: String) {
        _estado.update { it.copy(cidade = texto) }
    }

    fun buscar() {
        val cidade = _estado.value.cidade
        if (cidade.isBlank()) {
            _estado.update { it.copy(erro = "Digite o nome de uma cidade.") }
            return
        }

        buscaAtual?.cancel()
        buscaAtual = viewModelScope.launch {
            _estado.update { it.copy(carregando = true, erro = null) }
            try {
                val resultado = repositorio.obterPrevisao(cidade)
                _estado.update {
                    it.copy(
                        carregando = false,
                        previsao = resultado.previsao,
                        dias = agruparPorDia(resultado.previsao.horarios),
                        offline = resultado.offline
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: ErroPrevisao) {
                _estado.update { it.copy(carregando = false, erro = e.message) }
            } catch (e: Throwable) {
                _estado.update { it.copy(carregando = false, erro = ErroPrevisao.Desconhecido().message) }
            }
        }
    }
}
