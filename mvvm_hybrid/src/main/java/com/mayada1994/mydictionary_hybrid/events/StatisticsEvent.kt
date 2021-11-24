package com.mayada1994.mydictionary_hybrid.events

import com.mayada1994.mydictionary_hybrid.entities.Statistics

sealed class StatisticsEvent {
    data class SetStats(val stats: List<Statistics>) : ViewEvent
}