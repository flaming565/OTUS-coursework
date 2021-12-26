package com.beautydiary.featurestatistics.vm

import androidx.lifecycle.viewModelScope
import com.beautydiary.domain.models.DomainCategoryWithTasks
import com.beautydiary.domain.usecases.CategoryActionsUseCase
import com.beautydiary.core_ui.ext.cast
import com.beautydiary.domain.common.Result
import com.beautydiary.core_ui.ext.map
import com.beautydiary.core_ui.ext.toMonthDate
import com.beautydiary.core_ui.vm.BaseViewModel
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.*


internal class StatisticsViewModel : BaseViewModel() {
    private val categoryUseCase by inject<CategoryActionsUseCase>()

    val categories: StateFlow<List<DomainCategoryWithTasks>> = categoryUseCase.allWithTasks()
        .flatMapMerge { mapGetCategoriesResult(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    private val calendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val currentDate = MutableStateFlow(calendar.timeInMillis)
    val currentMonth = currentDate.map(viewModelScope) {
        SimpleDateFormat("LLLL", Locale.getDefault()).format(it)
            .replaceFirstChar(Char::uppercase)
    }
    val currentYear = currentDate.map(viewModelScope) {
        SimpleDateFormat("yyyy", Locale.getDefault()).format(it)
    }

    val isNextMonthAvailable = currentDate.map(viewModelScope) {
        Calendar.getInstance().timeInMillis.toMonthDate() != it.toMonthDate()
    }

    val isPreviousMonthAvailable = currentDate.map(viewModelScope) { date ->
        categories.value.isEmpty() || categories.value.any { it.category.creationDate < date }
    }

    fun nextMonth() {
        calendar.timeInMillis = currentDate.value
        calendar.add(Calendar.MONTH, 1)

        currentDate.value = calendar.timeInMillis
    }

    fun previousMonth() {
        calendar.timeInMillis = currentDate.value
        calendar.add(Calendar.MONTH, -1)

        currentDate.value = calendar.timeInMillis
    }

    private fun mapGetCategoriesResult(result: Result): Flow<List<DomainCategoryWithTasks>> {
        return mapResponseResult(result)?.let { cast<Flow<List<DomainCategoryWithTasks>>>(it) }
            ?: flowOf(emptyList())
    }
}