package com.example.previsaotempo

import com.example.previsaotempo.dominio.PrevisaoHorario
import com.example.previsaotempo.util.agruparPorDia
import com.example.previsaotempo.util.diaDaSemana
import com.example.previsaotempo.util.TipoClima
import com.example.previsaotempo.util.tipoDoClima
import com.example.previsaotempo.util.formatarDia
import com.example.previsaotempo.util.normalizarCidade
import com.example.previsaotempo.util.removerAcentos
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilTest {

    @Test
    fun removeAcentosMantendoMaiusculas() {
        assertEquals("Goiania", removerAcentos("Goiânia"))
        assertEquals("SAO JOAO", removerAcentos("SÃO JOÃO"))
        assertEquals("Florianopolis", removerAcentos("Florianópolis"))
    }

    @Test
    fun normalizaCidadeComPaisPadrao() {
        assertEquals("Sao Paulo,BR", normalizarCidade("  São   Paulo "))
        assertEquals("Lisboa,PT", normalizarCidade("Lisboa,PT"))
    }

    @Test
    fun calculaDiaDaSemana() {
        assertEquals(4, diaDaSemana("2026-11-26")) // quinta-feira
        assertEquals(0, diaDaSemana("2026-11-29")) // domingo
        assertEquals(1, diaDaSemana("2024-01-01")) // segunda-feira
        assertEquals(4, diaDaSemana("2024-02-29")) // ano bissexto
    }

    @Test
    fun formataDia() {
        assertEquals("Quinta, 26/11", formatarDia("2026-11-26"))
        assertEquals("texto-invalido", formatarDia("texto-invalido"))
    }

    @Test
    fun agrupaHorariosPorDia() {
        val horarios = listOf(
            horario("2026-11-27 03:00:00", min = 18.0, max = 20.0),
            horario("2026-11-26 21:00:00", min = 22.0, max = 24.0),
            horario("2026-11-26 18:00:00", min = 25.0, max = 29.5),
        )

        val dias = agruparPorDia(horarios)

        assertEquals(listOf("2026-11-26", "2026-11-27"), dias.map { it.data })
        assertEquals(22.0, dias[0].minima)
        assertEquals(29.5, dias[0].maxima)
        assertEquals(listOf("18:00", "21:00"), dias[0].horarios.map { it.hora })
    }

    @Test
    fun identificaTipoDeClimaPeloIcone() {
        assertEquals(TipoClima.SOL, tipoDoClima("01d"))
        assertEquals(TipoClima.LUA, tipoDoClima("01n"))
        assertEquals(TipoClima.CHUVA, tipoDoClima("09n"))
        assertEquals(TipoClima.TEMPESTADE, tipoDoClima("11d"))
        assertEquals(TipoClima.NUBLADO, tipoDoClima(""))
    }

    private fun horario(dataHora: String, min: Double, max: Double) = PrevisaoHorario(
        dataHora = dataHora,
        temperatura = (min + max) / 2,
        sensacao = max,
        minima = min,
        maxima = max,
        umidade = 60,
        vento = 2.0,
        descricao = "céu limpo",
        icone = "01d"
    )
}
