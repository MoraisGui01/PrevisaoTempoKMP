package com.example.previsaotempo.dados.local

import com.example.previsaotempo.dominio.Previsao

// Guarda a última previsão de cada cidade para uso sem internet.
//  - Android e Desktop: Room (src/roomMain)
//  - Web: localStorage do navegador (src/wasmJsMain), porque o Room não roda em Wasm
interface PrevisaoCache {

    // Troca a previsão guardada da cidade pela nova
    suspend fun salvar(chave: String, previsao: Previsao)

    // Devolve a previsão guardada ou null se a cidade nunca foi buscada
    suspend fun buscar(chave: String): Previsao?
}
