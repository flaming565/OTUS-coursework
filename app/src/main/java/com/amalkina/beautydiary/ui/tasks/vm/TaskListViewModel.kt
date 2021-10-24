package com.amalkina.beautydiary.ui.tasks.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amalkina.beautydiary.domain.common.Event
import com.amalkina.beautydiary.domain.common.Result
import com.amalkina.beautydiary.domain.models.DomainCategory
import com.amalkina.beautydiary.domain.models.DomainCategoryWithTasks
import com.amalkina.beautydiary.domain.usecases.CategoryActionsUseCase
import com.amalkina.beautydiary.domain.usecases.TaskActionsUseCase
import com.amalkina.beautydiary.ui.common.ext.map
import com.amalkina.beautydiary.ui.common.ext.mapToMutable
import com.amalkina.beautydiary.ui.common.ext.toUIModel
import com.amalkina.beautydiary.ui.common.ext.tryCast
import com.amalkina.beautydiary.ui.common.vm.BaseViewModel
import com.amalkina.beautydiary.ui.tasks.models.CategoryTask
import com.amalkina.beautydiary.ui.tasks.models.CategoryTaskNew
import kotlinx.coroutines.flow.*
import org.koin.core.component.inject


internal class TaskListViewModel(private val category: DomainCategory) : BaseViewModel() {
    private val taskUseCase by inject<TaskActionsUseCase>()
    private val categoryUseCase by inject<CategoryActionsUseCase>()

    val titleFragment = category.name
    val isFragmentLoading = isLoading.mapToMutable(viewModelScope) { it }
    val userActionEvent = MutableLiveData<Event<UserAction>>()

    private val rawCategoryWithTasks = categoryUseCase.getWithTasks(category.id)
        .flatMapMerge { mapGetTasksResult(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    val categoryTasks = rawCategoryWithTasks.map(viewModelScope) { mapCategoryTasks(it) }

    fun onTaskClick(id: Long) {
        userActionEvent.postValue(Event(UserAction.OnClickTask(id)))
    }

    fun onAddTask() {
        userActionEvent.postValue(Event(UserAction.AddTask(category.id)))
    }

    fun onEditTask() {
        userActionEvent.postValue(Event(UserAction.EditTask))
    }

    fun onDeleteTask() {
        userActionEvent.postValue(Event(UserAction.DeleteTask))
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

    private fun mapCategoryTasks(rawData: DomainCategoryWithTasks?): List<CategoryTask> {
        return rawData?.let {
            it.tasks.map { task -> task.toUIModel() } + listOf(CategoryTaskNew)
        } ?: emptyList()
    }


    sealed class UserAction {
        class OnClickTask(val id: Long) : UserAction()
        class AddTask(val categoryId: Long) : UserAction()
        object DeleteTask : UserAction()
        object EditTask : UserAction()
    }

}