package com.amalkina.beautydiary.ui.common.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import timber.log.Timber

@KoinApiExtension
internal open class BaseViewModel : ViewModel(), CoroutineScope, KoinComponent {

    private val exceptionHandler = CoroutineExceptionHandler { _, t ->
        Timber.e(t, "Exception handled.")
    }
    override val coroutineContext = viewModelScope.coroutineContext + exceptionHandler
}
