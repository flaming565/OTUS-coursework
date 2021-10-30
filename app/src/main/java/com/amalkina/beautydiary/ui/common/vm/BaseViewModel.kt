package com.amalkina.beautydiary.ui.common.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.domain.common.Event
import com.amalkina.beautydiary.domain.common.Result
import com.amalkina.beautydiary.ui.common.models.BaseModel
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

    val isLoading = MutableLiveData(false)
    val errorEvent = MutableLiveData<Event<String>>()

    fun mapResponseResult(
        result: Result,
        errorMessage: String? = BaseModel.getString(R.string.add_category_error_database),
        onSuccess: (() -> Unit)? = null
    ): Any? {
        return when (result) {
            is Result.Success<*> -> {
                onSuccess?.invoke()
                isLoading.value = false
                result.value
            }
            is Result.Error -> {
                Timber.d("Map response error: ${result.message}")
                val message = errorMessage ?: result.message
                errorEvent.postValue(Event(message))
                isLoading.value = false
                null
            }
            Result.Loading -> {
                isLoading.value = true
            }
        }
    }
}
