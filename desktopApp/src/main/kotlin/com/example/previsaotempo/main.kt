package com.example.previsaotempo

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.example.previsaotempo.di.iniciarKoin

fun main() {
    iniciarKoin()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Previsão do Tempo",
            state = rememberWindowState(width = 520.dp, height = 860.dp)
        ) {
            App()
        }
    }
}
