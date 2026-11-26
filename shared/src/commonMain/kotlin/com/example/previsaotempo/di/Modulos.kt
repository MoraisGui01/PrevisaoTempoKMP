package com.example.previsaotempo.di

import com.example.previsaotempo.Configuracao
import com.example.previsaotempo.apresentacao.PrevisaoViewModel
import com.example.previsaotempo.dados.remoto.OpenWeatherService
import com.example.previsaotempo.dados.remoto.criarClienteHttp
import com.example.previsaotempo.repositorio.PrevisaoRepository
import com.example.previsaotempo.repositorio.PrevisaoRepositoryImpl
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

// Dependências iguais nas três plataformas
val moduloComum = module {
    single { criarClienteHttp() }
    single { OpenWeatherService(cliente = get(), chaveApi = Configuracao.CHAVE_OPENWEATHER) }
    single<PrevisaoRepository> { PrevisaoRepositoryImpl(servico = get(), cache = get()) }
    viewModelOf(::PrevisaoViewModel)
}

// Cada plataforma informa como guardar a previsão localmente (PrevisaoCache)
expect fun moduloPlataforma(): Module

// Inicia o Koin. O Android usa o parâmetro para passar o Context.
fun iniciarKoin(configuracao: KoinAppDeclaration = {}): KoinApplication =
    startKoin {
        configuracao()
        modules(moduloComum, moduloPlataforma())
    }
