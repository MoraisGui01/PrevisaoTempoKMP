package com.example.previsaotempo

import android.app.Application
import com.example.previsaotempo.di.iniciarKoin
import org.koin.android.ext.koin.androidContext

class PrevisaoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // O Room precisa do Context para saber onde criar o arquivo do banco
        iniciarKoin { androidContext(this@PrevisaoApplication) }
    }
}
