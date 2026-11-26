package com.example.previsaotempo.dados.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.previsaotempo.dominio.Previsao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

const val NOME_BANCO = "previsao.db"

@Database(entities = [HorarioEntity::class], version = 1)
@ConstructedBy(BancoPrevisaoConstrutor::class)
abstract class BancoPrevisao : RoomDatabase() {
    abstract fun previsaoDao(): PrevisaoDao
}

// O compilador do Room gera o "actual" deste objeto para Android e Desktop
@Suppress("KotlinNoActualForExpect")
expect object BancoPrevisaoConstrutor : RoomDatabaseConstructor<BancoPrevisao> {
    override fun initialize(): BancoPrevisao
}

// Parte comum da criação do banco. Cada plataforma só informa onde fica o arquivo.
fun criarBanco(builder: RoomDatabase.Builder<BancoPrevisao>): BancoPrevisao =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

// Implementação do PrevisaoCache usando o Room
class RoomPrevisaoCache(private val dao: PrevisaoDao) : PrevisaoCache {

    override suspend fun salvar(chave: String, previsao: Previsao) {
        dao.substituir(chave, previsao.paraEntidades(chave))
    }

    override suspend fun buscar(chave: String): Previsao? =
        dao.buscar(chave).paraPrevisao()
}
