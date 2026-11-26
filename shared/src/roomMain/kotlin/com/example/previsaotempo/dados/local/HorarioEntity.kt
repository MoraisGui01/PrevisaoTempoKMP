package com.example.previsaotempo.dados.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.previsaotempo.dominio.Previsao
import com.example.previsaotempo.dominio.PrevisaoHorario

// Tabela "horario_previsao": cada linha é um horário (de 3 em 3 horas) da previsão de uma cidade.
// "chave" é o texto pesquisado já normalizado (ex.: "Goiania,BR").
@Entity(
    tableName = "horario_previsao",
    indices = [Index("chave")]
)
data class HorarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chave: String,
    val cidade: String,
    val pais: String,
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

fun Previsao.paraEntidades(chave: String): List<HorarioEntity> = horarios.map { h ->
    HorarioEntity(
        chave = chave,
        cidade = cidade,
        pais = pais,
        dataHora = h.dataHora,
        temperatura = h.temperatura,
        sensacao = h.sensacao,
        minima = h.minima,
        maxima = h.maxima,
        umidade = h.umidade,
        vento = h.vento,
        descricao = h.descricao,
        icone = h.icone
    )
}

fun List<HorarioEntity>.paraPrevisao(): Previsao? {
    val primeira = firstOrNull() ?: return null
    return Previsao(
        cidade = primeira.cidade,
        pais = primeira.pais,
        horarios = map {
            PrevisaoHorario(
                dataHora = it.dataHora,
                temperatura = it.temperatura,
                sensacao = it.sensacao,
                minima = it.minima,
                maxima = it.maxima,
                umidade = it.umidade,
                vento = it.vento,
                descricao = it.descricao,
                icone = it.icone
            )
        }
    )
}
