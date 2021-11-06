package com.mayada1994.mydictionary.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    foreignKeys = [ForeignKey(
        entity = Language::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("language"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Word(
    @PrimaryKey val name: String,
    val translation: String,
    val language: Int
)