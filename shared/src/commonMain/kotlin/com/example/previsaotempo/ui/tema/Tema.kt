package com.example.previsaotempo.ui.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta em tons de azul, lembrando céu limpo
private val Azul = Color(0xFF1565C0)
private val AzulClaro = Color(0xFF90CAF9)
private val Ceu = Color(0xFFE3F2FD)
private val Noite = Color(0xFF0D1B2A)
private val NoiteCartao = Color(0xFF1B263B)
private val Sol = Color(0xFFFFB300)

private val CoresClaras = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = Sol,
    background = Ceu,
    surface = Ceu,
    surfaceVariant = Color.White
)

private val CoresEscuras = darkColorScheme(
    primary = AzulClaro,
    onPrimary = Noite,
    primaryContainer = Color(0xFF1E3A5F),
    onPrimaryContainer = Color(0xFFE3F2FD),
    secondary = Sol,
    background = Noite,
    surface = Noite,
    surfaceVariant = NoiteCartao
)

@Composable
fun PrevisaoTempoTema(
    escuro: Boolean = isSystemInDarkTheme(),
    conteudo: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (escuro) CoresEscuras else CoresClaras,
        content = conteudo
    )
}
