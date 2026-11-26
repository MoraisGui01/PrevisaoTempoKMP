package com.example.previsaotempo.ui.componentes

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.previsaotempo.dominio.Previsao
import com.example.previsaotempo.dominio.PrevisaoHorario
import com.example.previsaotempo.dominio.ResumoDia
import com.example.previsaotempo.util.formatarDia
import com.example.previsaotempo.util.primeiraMaiuscula
import kotlin.math.roundToInt

private fun graus(valor: Double) = "${valor.roundToInt()}°"

// Destaque com o primeiro horário da previsão (o mais próximo de agora)
@Composable
fun CartaoAgora(previsao: Previsao) {
    val agora = previsao.horarios.firstOrNull() ?: return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = if (previsao.pais.isBlank()) previsao.cidade else "${previsao.cidade}, ${previsao.pais}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconeClima(codigo = agora.icone, tamanho = 72.dp)
                Column(Modifier.padding(start = 12.dp)) {
                    Text(text = "${agora.temperatura.roundToInt()} °C", fontSize = 44.sp, fontWeight = FontWeight.Bold)
                    Text(text = agora.descricao.primeiraMaiuscula())
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Informacao("Sensação", graus(agora.sensacao))
                Informacao("Umidade", "${agora.umidade}%")
                Informacao("Vento", "${(agora.vento * 3.6).roundToInt()} km/h")
            }
        }
    }
}

@Composable
private fun Informacao(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = titulo, style = MaterialTheme.typography.labelMedium)
        Text(text = valor, fontWeight = FontWeight.Bold)
    }
}

// Um dia da previsão com mínima, máxima e os horários em uma faixa rolável
@Composable
fun CartaoDia(dia: ResumoDia) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = formatarDia(dia.data), fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(text = "${graus(dia.minima)} / ${graus(dia.maxima)}", fontWeight = FontWeight.SemiBold)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dia.horarios.forEach { ItemHorario(it) }
            }
        }
    }
}

@Composable
private fun ItemHorario(horario: PrevisaoHorario) {
    Column(
        modifier = Modifier.width(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = horario.hora, style = MaterialTheme.typography.labelMedium)
        IconeClima(codigo = horario.icone, tamanho = 36.dp)
        Text(text = graus(horario.temperatura), fontWeight = FontWeight.Bold)
        Text(
            text = "${horario.umidade}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun AvisoOffline() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Text(
            text = "Sem conexão: mostrando a última previsão salva desta cidade.",
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
fun MensagemErro(mensagem: String, aoTentarNovamente: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = mensagem,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        OutlinedButton(onClick = aoTentarNovamente) {
            Text("Tentar novamente")
        }
    }
}
