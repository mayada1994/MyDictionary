package com.mayada1994.mydictionary.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "statistics",
    foreignKeys = [ForeignKey(
        entity = Language::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("language"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Statistics(
    @PrimaryKey val id: Int,
    val timestamp: Long,
    val language: Int
)