package com.amalkina.beautydiary.ui.common.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amalkina.beautydiary.domain.common.Event
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import timber.log.Timber


internal open class BaseViewModel : ViewModel(), CoroutineScope, KoinComponent {

    private val exceptionHandler = CoroutineExceptionHandler { _, t ->
        Timber.e(t, "Exception handled.")
    }
    override val coroutineContext = viewModelScope.coroutineContext + exceptionHandler

    val isLoading = MutableStateFlow(false)
    val errorEvent = MutableLiveData<Event<String>>()
}
