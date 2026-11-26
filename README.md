# Previsão do Tempo — Kotlin Multiplatform

Avaliação II de Programação para Dispositivos Móveis: o app de previsão do tempo da
Avaliação I feito em **Kotlin Multiplatform (KMP)**, com o mesmo código rodando em
**Android, Desktop e Web**, e **Room** guardando os dados recebidos da API para uso sem internet.

## Funcionalidades

- Busca da previsão de 5 dias (de 3 em 3 horas) na API da **OpenWeather**
- Destaque com temperatura atual, sensação térmica, umidade e vento
- Previsão agrupada por dia, com mínima, máxima e os horários em uma faixa rolável
- Cidades com acento funcionam ("Goiânia", "São Paulo"); dá para informar outro país ("Lisboa,PT")
- **Modo offline**: sem conexão, o app mostra a última previsão salva da cidade
- Mensagens de erro específicas: cidade não encontrada, chave inválida e sem conexão
- Tema claro e escuro, seguindo o sistema

## Tecnologias

| Item | Uso |
|---|---|
| Compose Multiplatform | Uma única tela para as três plataformas |
| Ktor Client | Requisições HTTP (substitui o Retrofit, que só existe no Android/JVM) |
| kotlinx.serialization | Conversão do JSON da API |
| Koin | Injeção de dependências |
| Room (KMP) | Banco local no Android e no Desktop |
| ViewModel + StateFlow | Arquitetura MVVM |

## Configurando a chave da API

Crie (ou edite) o arquivo `local.properties` na raiz do projeto:

```properties
OPENWEATHER_API_KEY=sua_chave_aqui
```

O Android Studio também grava o `sdk.dir` nesse arquivo. Ele está no `.gitignore`,
então a chave não vai para o GitHub. Na compilação, o Gradle gera o objeto
`Configuracao` com a chave, que pode ser lido nas três plataformas.

## Como rodar

| Plataforma | Comando |
|---|---|
| Android | `./gradlew :androidApp:assembleDebug` (ou o botão Run do Android Studio) |
| Desktop | `./gradlew :desktopApp:run` |
| Web (Wasm) | `./gradlew :webApp:wasmJsBrowserDevelopmentRun` |
| Testes | `./gradlew :shared:jvmTest` |

## Estrutura

```
shared/src/
  commonMain/                      código usado pelas 3 plataformas
    dominio/Previsao.kt              modelos do app e erros tratados
    dados/remoto/                    OpenWeatherService (Ktor), DTOs e conversão para o domínio
    dados/local/PrevisaoCache.kt     interface do armazenamento local
    repositorio/                     PrevisaoRepository: API primeiro, cache como reserva
    apresentacao/                    PrevisaoViewModel e PrevisaoUiState (MVVM)
    ui/                              tela, cartões e tema em Compose
    util/                            acentos, datas e ícones
    di/Modulos.kt                    módulos do Koin
  roomMain/                        Room, compartilhado entre Android e Desktop
    BancoPrevisao, PrevisaoDao, HorarioEntity
  androidMain/ jvmMain/ wasmJsMain/  o que muda em cada plataforma
  commonTest/                      testes de utilitários e do repositório
androidApp/   desktopApp/   webApp/   pontos de entrada de cada plataforma
```

## Como o armazenamento local funciona

1. O `PrevisaoRepositoryImpl` pede a previsão para a API.
2. Deu certo: a previsão é gravada pelo `PrevisaoCache` e exibida.
3. Falhou por falta de rede: o repositório busca a última previsão salva da cidade
   e a tela mostra o aviso **"Sem conexão"**.
4. A API respondeu com erro (cidade inexistente, chave inválida): a mensagem é exibida,
   pois dados antigos não resolvem o problema.

No **Android** e no **Desktop**, o `PrevisaoCache` usa o **Room**: tabela `horario_previsao`,
e a troca da previsão antiga pela nova acontece em uma única transação (`@Transaction`).
Na **Web**, o Room ainda não publica versão para Kotlin/Wasm, então a mesma interface é
implementada com o `localStorage` do navegador, e os dados continuam salvos ao recarregar a página.

## Mudanças em relação à Avaliação I

- Retrofit → **Ktor**
- Jetpack Compose → **Compose Multiplatform**
- `BuildConfig` → objeto `Configuracao` gerado pelo Gradle
- `java.text.Normalizer` → função própria para remover acentos
- `java.time` → cálculo próprio do dia da semana (congruência de Zeller)
- Banco local com **Room** e modo offline
