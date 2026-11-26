import java.util.Properties
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

// A chave da OpenWeather fica no local.properties (fora do Git).
// Como BuildConfig só existe no Android, o Gradle gera um objeto Kotlin
// comum com a chave, que pode ser lido em todas as plataformas.
val chaveApi: String = Properties().run {
    val arquivo = rootProject.file("local.properties")
    if (arquivo.exists()) arquivo.inputStream().use { load(it) }
    getProperty("OPENWEATHER_API_KEY", "")
}

val gerarConfiguracao by tasks.registering {
    val pastaSaida = layout.buildDirectory.dir("generated/configuracao/kotlin")
    val chave = chaveApi
    inputs.property("chave", chave)
    outputs.dir(pastaSaida)
    doLast {
        val arquivo = pastaSaida.get().file("com/example/previsaotempo/Configuracao.kt").asFile
        arquivo.parentFile.mkdirs()
        arquivo.writeText(
            """
            |package com.example.previsaotempo
            |
            |// Gerado pelo Gradle a partir do local.properties. Não edite à mão.
            |internal object Configuracao {
            |    const val CHAVE_OPENWEATHER: String = "$chave"
            |}
            |""".trimMargin()
        )
    }
}

kotlin {
    // O Room usa expect/actual em objeto (BancoPrevisaoConstrutor), recurso ainda em Beta
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    android {
        namespace = "com.example.previsaotempo.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(gerarConfiguracao)
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.uiToolingPreview)
                api(libs.androidx.lifecycle.viewmodelCompose)
                api(libs.androidx.lifecycle.runtimeCompose)

                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.serialization.kotlinxJson)

                api(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
            }
        }

        // Room só publica para Android e JVM. Este source set é usado
        // pelas duas plataformas e fica invisível para a versão Web.
        val roomMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.sqlite.bundled)
            }
        }

        androidMain {
            dependsOn(roomMain)
            dependencies {
                implementation(libs.ktor.client.android)
                implementation(libs.koin.android)
                implementation(libs.compose.uiTooling)
            }
        }

        jvmMain {
            dependsOn(roomMain)
            dependencies {
                implementation(libs.ktor.client.java)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

// Compilador do Room (gera BancoPrevisao_Impl e PrevisaoDao_Impl)
dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
