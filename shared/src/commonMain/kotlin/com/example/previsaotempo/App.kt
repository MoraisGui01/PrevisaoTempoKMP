package com.example.previsaotempo

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.previsaotempo.ui.PrevisaoScreen
import com.example.previsaotempo.ui.tema.PrevisaoTempoTema

// Ponto de entrada da interface, o mesmo para Android, Desktop e Web
@Composable
fun App() {
    PrevisaoTempoTema {
        Surface {
            PrevisaoScreen()
        }
    }
}
