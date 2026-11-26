package com.example.previsaotempo.dados.remoto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Classes que espelham o JSON do endpoint /data/2.5/forecast da OpenWeather.
// Só os campos usados pelo app estão declarados.

@Serializable
data class PrevisaoResponseDto(
    val list: List<ItemPrevisaoDto>,
    val city: CidadeDto
)

@Serializable
data class CidadeDto(
    val name: String,
    val country: String = ""
)

@Serializable
data class ItemPrevisaoDto(
    @SerialName("dt_txt") val dataHora: String,
    val main: PrincipalDto,
    val weather: List<CondicaoDto> = emptyList(),
    val wind: VentoDto = VentoDto()
)

@Serializable
data class PrincipalDto(
    val temp: Double,
    @SerialName("feels_like") val sensacao: Double = temp,
    @SerialName("temp_min") val minima: Double = temp,
    @SerialName("temp_max") val maxima: Double = temp,
    val humidity: Int = 0
)

@Serializable
data class CondicaoDto(
    val description: String = "",
    val icon: String = ""
)

@Serializable
data class VentoDto(
    val speed: Double = 0.0
)
