package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo
import com.mayada1994.mydictionary_hybrid.utils.SingleLiveEvent
import com.mayada1994.mydictionary_hybrid.utils.ViewEvent

abstract class BaseViewModel: ViewModel() {

    sealed class BaseEvent {

        data class SetDefaultLanguage(val defaultLanguage: LanguageInfo) : ViewEvent

        data class ShowProgress(val isProgressVisible: Boolean): ViewEvent

        data class ShowPlaceholder(val isVisible: Boolean): ViewEvent

        data class ShowMessage(@StringRes val resId: Int): ViewEvent

    }

    private val _event = SingleLiveEvent<ViewEvent>()
    val event: LiveData<ViewEvent>
        get() = _event

    fun setEvent(event: ViewEvent) {
        _event.value = event
    }

}