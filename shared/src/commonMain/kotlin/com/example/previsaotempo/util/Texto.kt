package com.example.previsaotempo.util

// java.text.Normalizer não existe no Kotlin/Wasm, então a troca de acentos é manual
private val ACENTOS = mapOf(
    'á' to 'a', 'à' to 'a', 'â' to 'a', 'ã' to 'a', 'ä' to 'a',
    'é' to 'e', 'è' to 'e', 'ê' to 'e', 'ë' to 'e',
    'í' to 'i', 'ì' to 'i', 'î' to 'i', 'ï' to 'i',
    'ó' to 'o', 'ò' to 'o', 'ô' to 'o', 'õ' to 'o', 'ö' to 'o',
    'ú' to 'u', 'ù' to 'u', 'û' to 'u', 'ü' to 'u',
    'ç' to 'c', 'ñ' to 'n'
)

fun removerAcentos(texto: String): String = buildString {
    for (letra in texto) {
        val semAcento = ACENTOS[letra.lowercaseChar()]
        when {
            semAcento == null -> append(letra)
            letra.isUpperCase() -> append(semAcento.uppercaseChar())
            else -> append(semAcento)
        }
    }
}

// "  São   Paulo " -> "Sao Paulo,BR". Se o usuário já informou o país ("Lisboa,PT"), mantém.
fun normalizarCidade(cidade: String): String {
    val limpa = removerAcentos(cidade.trim()).split(Regex("\\s+")).joinToString(" ")
    return if (limpa.contains(",")) limpa else "$limpa,BR"
}

fun String.primeiraMaiuscula(): String = replaceFirstChar { it.uppercase() }
