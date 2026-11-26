package com.example.previsaotempo.di

import androidx.room.Room
import com.example.previsaotempo.dados.local.BancoPrevisao
import com.example.previsaotempo.dados.local.NOME_BANCO
import com.example.previsaotempo.dados.local.PrevisaoCache
import com.example.previsaotempo.dados.local.RoomPrevisaoCache
import com.example.previsaotempo.dados.local.criarBanco
import java.io.File
import org.koin.core.module.Module
import org.koin.dsl.module

// Desktop: o banco fica em <pasta do usuário>/.previsaotempo/previsao.db
actual fun moduloPlataforma(): Module = module {
    single {
        val pasta = File(System.getProperty("user.home"), ".previsaotempo").apply { mkdirs() }
        criarBanco(Room.databaseBuilder<BancoPrevisao>(name = File(pasta, NOME_BANCO).absolutePath))
    }
    single<PrevisaoCache> { RoomPrevisaoCache(get<BancoPrevisao>().previsaoDao()) }
}
