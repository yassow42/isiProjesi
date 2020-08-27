package com.example.isiprojesi

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Datalarim(
    @ColumnInfo
    var guncel_sicakliklar: Float
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
}