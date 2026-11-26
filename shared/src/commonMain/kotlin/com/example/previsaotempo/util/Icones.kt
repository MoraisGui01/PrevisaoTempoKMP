package com.example.previsaotempo.util

// Tipos de clima desenhados pelo app (ver ui/componentes/IconeClima.kt)
enum class TipoClima { SOL, LUA, POUCAS_NUVENS, NUBLADO, CHUVA, TEMPESTADE, NEVE, NEVOA }

// Converte o código de ícone da OpenWeather (ex.: "10d") no tipo de clima.
// Os ícones são desenhados com Canvas em vez de emoji ou imagem, porque a versão
// Web do Compose não tem fonte de emoji e assim não é preciso baixar figuras.
fun tipoDoClima(icone: String): TipoClima = when (icone.take(2)) {
    "01" -> if (icone.endsWith("n")) TipoClima.LUA else TipoClima.SOL
    "02" -> TipoClima.POUCAS_NUVENS
    "03", "04" -> TipoClima.NUBLADO
    "09", "10" -> TipoClima.CHUVA
    "11" -> TipoClima.TEMPESTADE
    "13" -> TipoClima.NEVE
    "50" -> TipoClima.NEVOA
    else -> TipoClima.NUBLADO
}
