package com.example.previsaotempo.ui.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.previsaotempo.util.TipoClima
import com.example.previsaotempo.util.tipoDoClima
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val Amarelo = Color(0xFFFFB300)
private val Nuvem = Color(0xFFB0BEC5)
private val NuvemEscura = Color(0xFF78909C)
private val Gota = Color(0xFF42A5F5)
private val Raio = Color(0xFFFFD54F)

// Ícone do clima desenhado com formas simples (círculos e linhas)
@Composable
fun IconeClima(codigo: String, tamanho: Dp = 40.dp) {
    val tipo = tipoDoClima(codigo)
    Canvas(Modifier.size(tamanho)) {
        when (tipo) {
            TipoClima.SOL -> sol(centro = center, raio = size.minDimension * 0.22f)
            TipoClima.LUA -> lua()
            TipoClima.POUCAS_NUVENS -> {
                sol(centro = Offset(size.width * 0.62f, size.height * 0.36f), raio = size.minDimension * 0.17f)
                nuvem(Nuvem, deslocamentoY = 0.1f)
            }
            TipoClima.NUBLADO -> nuvem(Nuvem)
            TipoClima.CHUVA -> {
                nuvem(NuvemEscura, deslocamentoY = -0.12f)
                listOf(0.35f, 0.5f, 0.65f).forEach { x -> traco(Gota, x, 0.68f, x - 0.05f, 0.85f) }
            }
            TipoClima.TEMPESTADE -> {
                nuvem(NuvemEscura, deslocamentoY = -0.12f)
                traco(Raio, 0.55f, 0.6f, 0.45f, 0.75f)
                traco(Raio, 0.45f, 0.75f, 0.55f, 0.75f)
                traco(Raio, 0.55f, 0.75f, 0.45f, 0.92f)
            }
            TipoClima.NEVE -> {
                nuvem(Nuvem, deslocamentoY = -0.12f)
                listOf(0.35f, 0.5f, 0.65f).forEach { x ->
                    drawCircle(Color.White, radius = size.minDimension * 0.04f, center = Offset(size.width * x, size.height * 0.78f))
                    drawCircle(Nuvem, radius = size.minDimension * 0.04f, center = Offset(size.width * x, size.height * 0.78f), style = Stroke(1f))
                }
            }
            TipoClima.NEVOA -> listOf(0.35f, 0.5f, 0.65f).forEach { y -> traco(Nuvem, 0.2f, y, 0.8f, y) }
        }
    }
}

private fun DrawScope.sol(centro: Offset, raio: Float) {
    drawCircle(Amarelo, radius = raio, center = centro)
    repeat(8) { i ->
        val angulo = i * PI / 4
        val inicio = Offset(centro.x + cos(angulo).toFloat() * raio * 1.35f, centro.y + sin(angulo).toFloat() * raio * 1.35f)
        val fim = Offset(centro.x + cos(angulo).toFloat() * raio * 1.75f, centro.y + sin(angulo).toFloat() * raio * 1.75f)
        drawLine(Amarelo, inicio, fim, strokeWidth = size.minDimension * 0.06f, cap = StrokeCap.Round)
    }
}

private fun DrawScope.lua() {
    val raio = size.minDimension * 0.3f
    // Um segundo círculo "recorta" o primeiro, formando a lua crescente
    val recorte = Path().apply {
        addOval(Rect(center = Offset(center.x + raio * 0.45f, center.y - raio * 0.3f), radius = raio * 0.85f))
    }
    clipPath(recorte, clipOp = ClipOp.Difference) {
        drawCircle(Color(0xFFFFCA28), radius = raio, center = center)
    }
}

private fun DrawScope.nuvem(cor: Color, deslocamentoY: Float = 0f) {
    val m = size.minDimension
    val y = size.height * (0.6f + deslocamentoY)
    drawCircle(cor, radius = m * 0.16f, center = Offset(size.width * 0.32f, y))
    drawCircle(cor, radius = m * 0.22f, center = Offset(size.width * 0.5f, y - m * 0.1f))
    drawCircle(cor, radius = m * 0.15f, center = Offset(size.width * 0.7f, y))
    drawRect(cor, topLeft = Offset(size.width * 0.32f, y - m * 0.02f), size = Size(size.width * 0.38f, m * 0.17f))
}

private fun DrawScope.traco(cor: Color, x1: Float, y1: Float, x2: Float, y2: Float) {
    drawLine(
        cor,
        Offset(size.width * x1, size.height * y1),
        Offset(size.width * x2, size.height * y2),
        strokeWidth = size.minDimension * 0.06f,
        cap = StrokeCap.Round
    )
}
