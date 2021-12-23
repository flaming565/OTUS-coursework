package com.beautydiary.feature_tasks.vm

import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beautydiary.domain.models.DomainTask
import com.beautydiary.domain.models.DomainTaskAndCategory
import com.beautydiary.core.domain.usecases.TaskActionsUseCase
import com.beautydiary.core.ui.common.ext.*
import com.beautydiary.core.domain.common.Result
import com.beautydiary.core.ui.common.utils.Event
import com.beautydiary.core.ui.common.vm.BaseViewModel
import com.beautydiary.feature_tasks.common.toUIModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject


internal class TaskDetailViewModel(taskId: Long) : BaseViewModel() {
    private val taskUseCase by inject<TaskActionsUseCase>()

    private val categoryTask: StateFlow<DomainTaskAndCategory?> = taskUseCase.categoryTask(taskId)
        .flatMapMerge { mapGetTasksResult(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    val domainTask = categoryTask.map(viewModelScope) { it?.task }
    val task = domainTask.map(viewModelScope) { it?.toUIModel() }
    val category = categoryTask.map(viewModelScope) { it?.category }

    val isChartVisible = domainTask.map(viewModelScope) { it != null }

    val isDoneButtonEnable = domainTask.map(viewModelScope) {
        it?.lastExecutionDate?.toDate() != System.currentTimeMillis().toDate()
    }
    val showCompleteEarlierButton = task.map(viewModelScope) {
        it != null && (it.lastExecutionDate.toStartOfDay() < System.currentTimeMillis()
            .toStartOfDay() - DateUtils.DAY_IN_MILLIS)
    }

    val taskCompleteEvent = MutableLiveData<Event<DomainTask>>()

    fun onDoneClick() {
        completeTask()
    }

    fun onCompleteEarlierClick() {
        domainTask.value?.let {
            taskCompleteEvent.postValue(Event(it))
        }
    }

    fun completeTask(date: Long = System.currentTimeMillis()) {
        domainTask.value?.let { completeTask(it, date) }
    }

    fun deleteTask() {
        categoryTask.value?.task?.let { deleteTask(it) }
    }

    private fun completeTask(task: DomainTask, date: Long) {
        launch {
            task.executionDateList.add(date)
            val result = taskUseCase.update(task)
            mapResponseResult(result)
        }
    }

    private fun deleteTask(task: DomainTask) {
        launch {
            val result = taskUseCase.delete(task)
            mapResponseResult(result)
        }
    }

    private fun mapGetTasksResult(result: Result): Flow<DomainTaskAndCategory?> {
        return mapResponseResult(result)?.let {
            cast<Flow<DomainTaskAndCategory>>(it)
        } ?: flowOf<DomainTaskAndCategory?>(null)
    }
}