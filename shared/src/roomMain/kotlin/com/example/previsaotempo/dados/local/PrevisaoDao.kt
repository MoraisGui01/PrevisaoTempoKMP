package com.example.previsaotempo.dados.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class PrevisaoDao {

    @Insert
    protected abstract suspend fun inserir(horarios: List<HorarioEntity>)

    @Query("DELETE FROM horario_previsao WHERE chave = :chave")
    protected abstract suspend fun apagar(chave: String)

    @Query("SELECT * FROM horario_previsao WHERE chave = :chave ORDER BY dataHora")
    abstract suspend fun buscar(chave: String): List<HorarioEntity>

    // Apaga a previsão antiga e grava a nova numa única transação:
    // se algo falhar no meio, a previsão anterior continua salva.
    @Transaction
    open suspend fun substituir(chave: String, horarios: List<HorarioEntity>) {
        apagar(chave)
        inserir(horarios)
    }
}
