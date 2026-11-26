package com.example.previsaotempo

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.example.previsaotempo.di.iniciarKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    iniciarKoin()
    ComposeViewport { App() }
}
