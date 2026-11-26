package com.example.previsaotempo.util

import com.example.previsaotempo.dominio.PrevisaoHorario
import com.example.previsaotempo.dominio.ResumoDia

private val DIAS_SEMANA = listOf("Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado")

// Dia da semana pela congruência de Zeller (evita depender de java.time, que não existe na Web).
// Recebe "AAAA-MM-DD" e devolve 0 = domingo ... 6 = sábado.
fun diaDaSemana(data: String): Int {
    val (anoTexto, mesTexto, diaTexto) = data.split("-")
    var ano = anoTexto.toInt()
    var mes = mesTexto.toInt()
    val dia = diaTexto.toInt()
    if (mes < 3) {
        mes += 12
        ano -= 1
    }
    val k = ano % 100
    val j = ano / 100
    val h = (dia + (13 * (mes + 1)) / 5 + k + k / 4 + j / 4 + 5 * j) % 7
    // Zeller devolve 0 = sábado; converte para 0 = domingo
    return (h + 6) % 7
}

// "2026-11-26" -> "Quinta, 26/11"
fun formatarDia(data: String): String = try {
    val (_, mes, dia) = data.split("-")
    "${DIAS_SEMANA[diaDaSemana(data)]}, $dia/$mes"
} catch (e: Exception) {
    data
}

// Agrupa os horários (de 3 em 3 horas) por dia, calculando mínima e máxima
fun agruparPorDia(horarios: List<PrevisaoHorario>): List<ResumoDia> =
    horarios
        .groupBy { it.data }
        .map { (data, doDia) ->
            ResumoDia(
                data = data,
                minima = doDia.minOf { it.minima },
                maxima = doDia.maxOf { it.maxima },
                horarios = doDia.sortedBy { it.dataHora }
            )
        }
        .sortedBy { it.data }
