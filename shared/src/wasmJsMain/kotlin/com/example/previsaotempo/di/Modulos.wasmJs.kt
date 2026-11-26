package com.example.previsaotempo.di

import com.example.previsaotempo.dados.local.LocalStoragePrevisaoCache
import com.example.previsaotempo.dados.local.PrevisaoCache
import org.koin.core.module.Module
import org.koin.dsl.module

// Web: o Room ainda não publica para Kotlin/Wasm, então a previsão vai para o localStorage
actual fun moduloPlataforma(): Module = module {
    single<PrevisaoCache> { LocalStoragePrevisaoCache() }
}
