package com.example.isiprojesi.servisler

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.isiprojesi.Datalarim

@Dao
interface Dao {
    @Insert
    suspend fun insertAll(vararg  data:Datalarim):List<Long>

    //Long listesi primaryKey

    @Query("SELECT * FROM datalarim")
    suspend fun getAllData():List<Datalarim>

    @Query("SELECT *FROM datalarim WHERE uuid=:dataID")
    suspend fun getOneData(dataID:Int):Datalarim

    @Query("DELETE FROM datalarim")
    suspend fun deleteAllData()



}