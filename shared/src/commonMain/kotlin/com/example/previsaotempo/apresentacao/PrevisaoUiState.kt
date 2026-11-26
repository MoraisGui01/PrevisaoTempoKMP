package com.example.previsaotempo.apresentacao

import com.example.previsaotempo.dominio.Previsao
import com.example.previsaotempo.dominio.ResumoDia

// Tudo o que a tela precisa para se desenhar
data class PrevisaoUiState(
    val cidade: String = "",
    val carregando: Boolean = false,
    val previsao: Previsao? = null,
    val dias: List<ResumoDia> = emptyList(),
    // A previsão exibida veio do armazenamento local (sem internet)
    val offline: Boolean = false,
    val erro: String? = null
)
