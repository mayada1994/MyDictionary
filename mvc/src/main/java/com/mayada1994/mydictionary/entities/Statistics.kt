package com.mayada1994.mydictionary.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "statistics",
    foreignKeys = [ForeignKey(
        entity = Language::class,
        parentColumns = arrayOf("code"),
        childColumns = arrayOf("language"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Statistics(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val result: String,
    val timestamp: Long,
    val language: String
)