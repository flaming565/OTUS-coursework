package com.beautydiary.feature_tasks.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.beautydiary.feature_tasks.R
import com.beautydiary.core.ui.common.utils.Event
import com.beautydiary.domain.models.*
import com.beautydiary.domain.models.DomainTask.Companion.DEFAULT_PROGRESS
import com.beautydiary.domain.models.DomainTask.Companion.MAX_PROGRESS
import com.beautydiary.domain.models.DomainTask.Companion.MIN_PROGRESS
import com.beautydiary.core.domain.usecases.CategoryActionsUseCase
import com.beautydiary.core.domain.usecases.TaskActionsUseCase
import com.beautydiary.core.ui.common.ext.*
import com.beautydiary.core.domain.common.Result
import com.beautydiary.core.ui.common.models.BaseModel
import com.beautydiary.core.ui.common.vm.BaseViewModel
import com.beautydiary.core.ui.home.models.HomeCategory
import com.beautydiary.feature_tasks.common.toUIModel
import com.beautydiary.feature_tasks.models.CategoryTask
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject


internal class AddTaskViewModel(categoryId: Long, taskId: Long) : BaseViewModel() {
    private val taskUseCase by inject<TaskActionsUseCase>()
    private val categoryUseCase by inject<CategoryActionsUseCase>()

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
    private val selectedTask =
        currentIndex.mapToMutable(viewModelScope) { baseTasks.value.getOrNull(it) }

    val taskTitle = selectedTask.mapToMutable(viewModelScope) { it?.name ?: "" }
    val taskPriority = selectedTask.mapToMutable(viewModelScope) { it?.priority?.toFloat() ?: 1F }
    val taskProgress =
        selectedTask.mapToMutable(viewModelScope) { it?.progress ?: DEFAULT_PROGRESS }
    val taskMaxProgress = MAX_PROGRESS
    val taskNote = selectedTask.mapToMutable(viewModelScope) { it?.note ?: "" }
    private val taskSchedule = selectedTask.map(viewModelScope) { it?.schedule ?: Schedule() }
    val taskScheduleValue = taskSchedule.mapToMutable(viewModelScope) { it.value.toString() }
    val isScheduleValueValid = taskScheduleValue.map(viewModelScope) { it.toInt() > 0 }
    private val taskFrequency = taskSchedule.mapToMutable(viewModelScope) { it.frequency }
    val taskFrequencyValue = taskFrequency.mapToMutable(viewModelScope) { it.name.lowercase() }
    val isIncreaseFrequencyAvailable = taskFrequency.map(viewModelScope) { it != Frequency.YEAR }
    val isDecreaseFrequencyAvailable = taskFrequency.map(viewModelScope) { it != Frequency.DAY }

    private val isTaskScheduleChanged =
        combine(taskSchedule, taskScheduleValue, taskFrequency) { schedule, value, frequency ->
            schedule.value.toString() != value || schedule.frequency != frequency
        }
    private val isTaskChanged = combine(
        selectedTask, taskTitle, taskProgress,
        taskNote, isTaskScheduleChanged
    ) { task, title, progress, note, isScheduleChanged ->
        task?.name != title || task.progress != progress || task.note.toString() != note || isScheduleChanged
    }

    val isSaveButtonEnabled = combine(
        taskTitle, taskScheduleValue,
        isTaskChanged, isLoading.asFlow()
    ) { title, schedule, isChanged, isLoading ->
        title.isNotEmpty() && schedule.toInt() > 0 && (isChanged || !isEditMode) && !isLoading
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )

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
        isLoading.value = true
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

    private fun createTask(): DomainTask {
        val currentProgress = when (taskProgress.value) {
            0 -> taskMaxProgress - MIN_PROGRESS
            else -> taskMaxProgress - taskProgress.value
        }

        return DomainTask(
            id = selectedTask.value?.id ?: 0,
            categoryId = category.value?.id ?: selectedTask.value?.categoryId ?: 0,
            name = taskTitle.value.trim(),
            priority = Priority.fromInt(taskPriority.value.toInt()),
            schedule = Schedule(taskScheduleValue.value.toInt(), taskFrequency.value),
            note = taskNote.value.trim(),
            startDate = DomainTask.calculateStartDate(
                currentProgress,
                Schedule(taskScheduleValue.value.toInt(), taskFrequency.value)
            )
        )
    }

    private fun saveTask(task: DomainTask) {
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