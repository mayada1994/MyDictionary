package com.mayada1994.mydictionary.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "languages")
data class Language(
    @PrimaryKey val id: Int,
    val code: String,
    var isDefault: Boolean = false
)
