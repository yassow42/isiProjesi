package com.example.isiprojesi.servisler

import android.content.Context
import androidx.room.Database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.isiprojesi.Datalarim

@Database(entities = arrayOf(Datalarim::class), version = 1)
abstract class Databasem : RoomDatabase() {

    abstract fun datalarinDaosu(): Dao

    //Singleton -> ??

    companion object {
        @Volatile
        private var instance: Databasem? = null

        private val lock = Any()

        //invoke -> ateşlemek instance var mı yok mu kontrol ediyoruz
        //instance avrsa sıkıntı yok yoksa senkronize bir şekilde ulasılmaya calısılack
        operator fun invoke(context: Context) = instance ?: synchronized(lock) {
            instance ?: makeDatabase(context).also {
                //also su demek bunu yap sonra sunuda yap

                instance = it
            }

        }

        private fun makeDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, Databasem::class.java, "Databasem"
        ).build()
    }

}