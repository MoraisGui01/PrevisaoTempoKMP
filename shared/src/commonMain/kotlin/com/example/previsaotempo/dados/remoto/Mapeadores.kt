package com.example.previsaotempo.dados.remoto

import com.example.previsaotempo.dominio.Previsao
import com.example.previsaotempo.dominio.PrevisaoHorario

fun PrevisaoResponseDto.paraDominio(): Previsao = Previsao(
    cidade = city.name,
    pais = city.country,
    horarios = list.map { it.paraDominio() }
)

fun ItemPrevisaoDto.paraDominio(): PrevisaoHorario {
    val condicao = weather.firstOrNull()
    return PrevisaoHorario(
        dataHora = dataHora,
        temperatura = main.temp,
        sensacao = main.sensacao,
        minima = main.minima,
        maxima = main.maxima,
        umidade = main.humidity,
        vento = wind.speed,
        descricao = condicao?.description.orEmpty(),
        icone = condicao?.icon.orEmpty()
    )
}
