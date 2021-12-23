package com.beautydiary.feature_tasks.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beautydiary.domain.models.DomainCategory
import com.beautydiary.domain.models.DomainCategoryWithTasks
import com.beautydiary.domain.models.DomainTask
import com.beautydiary.core.domain.usecases.CategoryActionsUseCase
import com.beautydiary.core.domain.usecases.TaskActionsUseCase
import com.beautydiary.core.ui.common.ext.tryCast
import com.beautydiary.core.ui.common.utils.Event
import com.beautydiary.core.domain.common.Result
import com.beautydiary.core.ui.common.ext.mapToMutable
import com.beautydiary.core.ui.common.ext.map
import com.beautydiary.core.ui.common.vm.BaseViewModel
import com.beautydiary.feature_tasks.common.toUIModel
import com.beautydiary.feature_tasks.models.CategoryTask
import com.beautydiary.feature_tasks.models.CategoryTaskNew
import com.beautydiary.feature_tasks.models.UserTaskAction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject


internal class TaskListViewModel(private val category: DomainCategory) : BaseViewModel() {
    private val taskUseCase by inject<TaskActionsUseCase>()
    private val categoryUseCase by inject<CategoryActionsUseCase>()

    val titleFragment = category.name
    val userActionEvent = MutableLiveData<Event<UserTaskAction>>()

    private val rawCategoryWithTasks = categoryUseCase.getWithTasks(category.id)
        .flatMapMerge { mapGetTasksResult(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    private val categoryTasks =
        rawCategoryWithTasks.mapToMutable(viewModelScope) { mapCategoryTasks(it) }
    val allTasks = categoryTasks.map(viewModelScope) { list ->
        list?.let { (it.toMutableList() + listOf(CategoryTaskNew)).toTypedArray() }
    }

    fun onTaskClick(id: Long) {
        userActionEvent.postValue(Event(UserTaskAction.OnClickTask(id)))
    }

    fun onAddTask() {
        userActionEvent.postValue(Event(UserTaskAction.AddTask(category.id)))
    }

    fun onEditTask(id: Long) {
        userActionEvent.postValue(Event(UserTaskAction.EditTask(id, category.id)))
    }

    fun onDeleteTask(id: Long, name: String) {
        userActionEvent.postValue(Event(UserTaskAction.DeleteTask(id, name)))
    }

    fun onCompleteTask(id: Long) {
        getTaskById(id)?.let {
            completeTask(it)
        }
    }

    fun deleteTask(id: Long) {
        getTaskById(id)?.let {
            deleteTask(it)
        }
    }

    fun sortByName() {
        sortCategoryTasks { a, b ->
            a.name.compareTo(b.name)
        }
    }

    fun sortByPriority() {
        sortCategoryTasks { a, b ->
            b.priority.compareTo(a.priority)
        }
    }

    fun sortByCreationDate() {
        sortCategoryTasks { a, b ->
            a.startDate.compareTo(b.startDate)
        }
    }

    fun sortByDueDate() {
        sortCategoryTasks { a, b ->
            a.daysRemaining.compareTo(b.daysRemaining)
        }
    }

    private fun sortCategoryTasks(comparator: Comparator<CategoryTask>) {
        val tasks = categoryTasks.value?.toMutableList() ?: return
        tasks.sortWith(comparator)
        categoryTasks.value = tasks.toTypedArray()
    }

    private fun getTaskById(id: Long): DomainTask? {
        return rawCategoryWithTasks.value?.tasks?.firstOrNull { it.id == id }
    }

    private fun completeTask(task: DomainTask) {
        launch {
            task.executionDateList.add(System.currentTimeMillis())
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

    private fun mapGetTasksResult(result: Result): Flow<DomainCategoryWithTasks?> {
        var resultFlow = flowOf<DomainCategoryWithTasks?>(null)
        mapResponseResult(result)?.let {
            tryCast<Flow<DomainCategoryWithTasks>>(it) {
                resultFlow = this
            }
        }
        return resultFlow
    }

    private fun mapCategoryTasks(rawData: DomainCategoryWithTasks?): Array<CategoryTask>? {
        return rawData?.tasks?.map { task -> task.toUIModel() }?.toTypedArray()
    }

}