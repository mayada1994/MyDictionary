package com.mayada1994.mydictionary_hybrid.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "languages")
data class Language(
    @PrimaryKey val code: String
) : Parcelable
