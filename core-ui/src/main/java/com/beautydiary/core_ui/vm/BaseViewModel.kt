package com.beautydiary.core_ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beautydiary.core_ui.R
import com.beautydiary.domain.common.Result
import com.beautydiary.core_ui.utils.Event
import com.beautydiary.core_ui.models.BaseModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import timber.log.Timber


open class BaseViewModel : ViewModel(), CoroutineScope, KoinComponent {

    private val exceptionHandler = CoroutineExceptionHandler { _, t ->
        Timber.e(t, "Exception handled.")
    }
    override val coroutineContext = viewModelScope.coroutineContext + exceptionHandler
    val errorEvent = MutableLiveData<Event<String>>()

    fun mapResponseResult(
        result: Result,
        errorMessage: String? = BaseModel.getString(R.string.common_database_error),
        onSuccess: (() -> Unit)? = null
    ): Any? {
        return when (result) {
            is Result.Success<*> -> {
                onSuccess?.invoke()
                result.value
            }
            is Result.Error -> {
                Timber.d("Map response error: ${result.message}")
                val message = errorMessage ?: result.message
                errorEvent.postValue(Event(message))
                null
            }
        }
    }
}
