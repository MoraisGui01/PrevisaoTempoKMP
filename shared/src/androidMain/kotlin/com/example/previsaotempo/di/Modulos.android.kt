package com.example.previsaotempo.di

import androidx.room.Room
import com.example.previsaotempo.dados.local.BancoPrevisao
import com.example.previsaotempo.dados.local.NOME_BANCO
import com.example.previsaotempo.dados.local.PrevisaoCache
import com.example.previsaotempo.dados.local.RoomPrevisaoCache
import com.example.previsaotempo.dados.local.criarBanco
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

// Android: o arquivo do banco fica na pasta de bancos de dados do próprio app
actual fun moduloPlataforma(): Module = module {
    single {
        val contexto = androidContext()
        criarBanco(
            Room.databaseBuilder<BancoPrevisao>(
                context = contexto,
                name = contexto.getDatabasePath(NOME_BANCO).absolutePath
            )
        )
    }
    single<PrevisaoCache> { RoomPrevisaoCache(get<BancoPrevisao>().previsaoDao()) }
}
