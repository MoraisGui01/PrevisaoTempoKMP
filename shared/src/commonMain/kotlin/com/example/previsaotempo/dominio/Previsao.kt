package com.example.previsaotempo.dominio

// Modelos usados pelo app (independentes da API e do banco)

data class Previsao(
    val cidade: String,
    val pais: String,
    val horarios: List<PrevisaoHorario>
)

data class PrevisaoHorario(
    // Formato recebido da API: "2026-11-26 15:00:00"
    val dataHora: String,
    val temperatura: Double,
    val sensacao: Double,
    val minima: Double,
    val maxima: Double,
    val umidade: Int,
    val vento: Double,
    val descricao: String,
    val icone: String
) {
    val data: String get() = dataHora.substringBefore(" ")
    val hora: String get() = dataHora.substringAfter(" ").take(5)
}

// Previsão de um dia, montada a partir dos horários de 3 em 3 horas
data class ResumoDia(
    val data: String,
    val minima: Double,
    val maxima: Double,
    val horarios: List<PrevisaoHorario>
)

// Erros que a tela sabe explicar para o usuário
sealed class ErroPrevisao(mensagem: String) : Exception(mensagem) {
    class CidadeNaoEncontrada : ErroPrevisao("Cidade não encontrada. Confira o nome digitado.")
    class ChaveInvalida : ErroPrevisao("Chave da API inválida. Confira o OPENWEATHER_API_KEY no local.properties.")
    class SemConexao : ErroPrevisao("Sem conexão e nenhuma previsão salva para esta cidade.")
    class Desconhecido : ErroPrevisao("Não foi possível carregar a previsão. Tente novamente.")
}
