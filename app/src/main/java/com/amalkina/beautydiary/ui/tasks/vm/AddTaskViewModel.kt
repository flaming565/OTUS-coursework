package com.amalkina.beautydiary.ui.tasks.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.domain.common.Event
import com.amalkina.beautydiary.domain.common.Result
import com.amalkina.beautydiary.domain.models.*
import com.amalkina.beautydiary.domain.usecases.CategoryActionsUseCase
import com.amalkina.beautydiary.domain.usecases.TaskActionsUseCase
import com.amalkina.beautydiary.ui.common.ext.*
import com.amalkina.beautydiary.ui.common.models.BaseModel
import com.amalkina.beautydiary.ui.common.vm.BaseViewModel
import com.amalkina.beautydiary.ui.home.models.HomeCategory
import com.amalkina.beautydiary.ui.tasks.models.CategoryTask
import com.amalkina.beautydiary.ui.tasks.models.CategoryTask.Companion.DEFAULT_PROGRESS
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject


internal class AddTaskViewModel(categoryId: Long, taskId: Long) : BaseViewModel() {
    private val taskUseCase by inject<TaskActionsUseCase>()
    private val categoryUseCase by inject<CategoryActionsUseCase>()

    val isFragmentLoading = isLoading.mapToMutable(viewModelScope) { it }
    val saveTaskEvent = MutableLiveData<Event<Unit>>()

    val isEditMode = taskId > 0
    val fragmentTitle = BaseModel.getString(
        when (isEditMode) {
            true -> R.string.common_edit
            else -> R.string.add_task_title
        }
    )

    init {
        if (isEditMode)
            loadEditableTask(taskId)
        else
            getCategory(categoryId)
    }

    private val category = MutableStateFlow<HomeCategory?>(null)
    private val baseTasks = category.flatMapMerge { getBaseTasks(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    val tasksNames = baseTasks.map(viewModelScope) { it.map { task -> task.name } }

    val currentIndex = MutableStateFlow(-1)
    private val selectedTask = currentIndex.mapToMutable(viewModelScope) { baseTasks.value.getOrNull(it) }

    val taskTitle = selectedTask.mapToMutable(viewModelScope) { it?.name ?: "" }
    val taskPriority = selectedTask.mapToMutable(viewModelScope) { it?.priority?.toFloat() ?: 1F }
    val taskProgress = selectedTask.mapToMutable(viewModelScope) { it?.progress ?: DEFAULT_PROGRESS }
    val taskNote = selectedTask.mapToMutable(viewModelScope) { it?.note ?: "" }
    private val taskSchedule = selectedTask.map(viewModelScope) { it?.schedule ?: Schedule() }
    val taskScheduleValue = taskSchedule.mapToMutable(viewModelScope) { it.value.toString() }
    private val taskFrequency = taskSchedule.mapToMutable(viewModelScope) { it.frequency }
    val taskFrequencyValue = taskFrequency.mapToMutable(viewModelScope) { it.name.lowercase() }
    val isIncreaseFrequencyAvailable = taskFrequency.map(viewModelScope) { it != Frequency.YEAR }
    val isDecreaseFrequencyAvailable = taskFrequency.map(viewModelScope) { it != Frequency.DAY }

    fun onSaveClick() {
        saveTask(createTask())
    }

    fun onDecreaseFrequencyClick() {
        taskFrequency.value = taskFrequency.value.previous()
    }

    fun onIncreaseFrequencyClick() {
        taskFrequency.value = taskFrequency.value.next()
    }

    private fun loadEditableTask(taskId: Long) {
        isFragmentLoading.value = true
        launch {
            val result = taskUseCase.get(taskId)
            val task = mapGetTaskResult(result)
            task?.toUIModel()?.let {
                selectedTask.value = it
            }
        }
    }

    private fun getCategory(categoryId: Long) {
        launch {
            val domainCategory = categoryUseCase.get(categoryId)
                .run { mapGetCategoryResult(this) }
            domainCategory?.toUIModel()?.let { category.value = it }
        }
    }

    private fun getBaseTasks(category: HomeCategory?): Flow<List<CategoryTask>> {
        return category?.let {
            taskUseCase.getBaseTasks(it.baseCategoryId)
                .flatMapMerge { result -> mapGetTasksResult(result) }
        } ?: flowOf(emptyList())
    }

    private fun createTask(): DomainTask = DomainTask(
        id = selectedTask.value?.id ?: 0,
        categoryId = category.value?.id ?: 0,
        name = taskTitle.value,
        priority = Priority.fromInt(taskPriority.value.toInt()),
        schedule = Schedule(taskScheduleValue.value.toInt(), taskFrequency.value),
        note = taskNote.value,
        // todo: calculate
        startDate = System.currentTimeMillis()
    )

    private fun saveTask(task: DomainTask) {
        isLoading.value = true
        launch {
            val result = if (isEditMode) taskUseCase.update(task)
            else taskUseCase.create(task)
            mapUpdateTaskResult(result)
        }
    }

    private fun mapGetTasksResult(result: Result): Flow<List<CategoryTask>> {
        var tasks: Flow<List<CategoryTask>> = flowOf(emptyList())
        mapResponseResult(result)?.let {
            tryCast<Flow<List<DomainTask>>>(it) {
                tasks = this.transform { list ->
                    emit(list.map { task -> task.toUIModel() })
                }
            }
        }
        return tasks
    }

    private fun mapGetCategoryResult(result: Result): DomainCategory? {
        return mapResponseResult(result)?.let { cast<DomainCategory>(it) }
    }

    private fun mapGetTaskResult(result: Result): DomainTask? {
        return mapResponseResult(result)?.let {
            cast<DomainTask>(it)
        }
    }

    private fun mapUpdateTaskResult(result: Result) {
        mapResponseResult(result) {
            saveTaskEvent.postValue(Event(Unit))
        }
    }
}