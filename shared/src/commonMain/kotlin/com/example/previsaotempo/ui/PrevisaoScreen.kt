package com.example.previsaotempo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.previsaotempo.apresentacao.PrevisaoUiState
import com.example.previsaotempo.apresentacao.PrevisaoViewModel
import com.example.previsaotempo.ui.componentes.AvisoOffline
import com.example.previsaotempo.ui.componentes.CartaoAgora
import com.example.previsaotempo.ui.componentes.CartaoDia
import com.example.previsaotempo.ui.componentes.MensagemErro
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PrevisaoScreen(viewModel: PrevisaoViewModel = koinViewModel()) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    PrevisaoConteudo(
        estado = estado,
        aoMudarCidade = viewModel::aoMudarCidade,
        aoBuscar = viewModel::buscar
    )
}

// Separada do ViewModel para facilitar o reuso e a visualização da tela
@Composable
fun PrevisaoConteudo(
    estado: PrevisaoUiState,
    aoMudarCidade: (String) -> Unit,
    aoBuscar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        // No Desktop e na Web a coluna fica limitada para não esticar na tela toda
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Previsão do Tempo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                CampoBusca(
                    cidade = estado.cidade,
                    habilitado = !estado.carregando,
                    aoMudarCidade = aoMudarCidade,
                    aoBuscar = aoBuscar
                )
            }

            when {
                estado.carregando -> item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                estado.erro != null -> item {
                    MensagemErro(mensagem = estado.erro, aoTentarNovamente = aoBuscar)
                }

                estado.previsao != null -> {
                    if (estado.offline) {
                        item { AvisoOffline() }
                    }
                    item {
                        CartaoAgora(previsao = estado.previsao)
                    }
                    items(estado.dias, key = { it.data }) { dia ->
                        CartaoDia(dia = dia)
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun CampoBusca(
    cidade: String,
    habilitado: Boolean,
    aoMudarCidade: (String) -> Unit,
    aoBuscar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = cidade,
            onValueChange = aoMudarCidade,
            label = { Text("Cidade") },
            placeholder = { Text("Ex.: Goiânia ou Lisboa,PT") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { aoBuscar() }),
            modifier = Modifier.weight(1f)
        )
        Column {
            Spacer(Modifier.height(8.dp))
            Button(onClick = aoBuscar, enabled = habilitado) {
                Text("Buscar")
            }
        }
    }
}
