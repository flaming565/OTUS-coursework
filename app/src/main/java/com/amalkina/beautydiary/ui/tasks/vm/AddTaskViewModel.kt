package com.amalkina.beautydiary.ui.tasks.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.amalkina.beautydiary.domain.common.Event
import com.amalkina.beautydiary.domain.models.Schedule
import com.amalkina.beautydiary.domain.models.Frequency
import com.amalkina.beautydiary.ui.common.vm.BaseViewModel
import com.amalkina.beautydiary.ui.tasks.models.TaskItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


internal class AddTaskViewModel(categoryId: Long, taskId: Long) : BaseViewModel() {

    val isEditMode = MutableLiveData(taskId >= 0)

    private val commonTasks = MutableStateFlow<List<TaskItem.Task>>(emptyList())
    val tasksNames = commonTasks.map { it.map { task -> task.title } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    val currentIndex = MutableStateFlow(-1)
    val currentTask = currentIndex.map { commonTasks.value.getOrElse(it) { getOtherTask() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    val taskTitle = MutableStateFlow("")
    val taskPriority = MutableStateFlow(1F)
    val taskProgress = MutableStateFlow(50)
    val taskNote = MutableStateFlow<String?>(null)

    val taskFrequency = MutableStateFlow("")
    private val taskFrequencyType = MutableStateFlow(Frequency.DAY)
    val taskFrequencyValue = taskFrequencyType.map { it.name.lowercase() }.asLiveData()
    val isIncreaseFrequencyAvailable = taskFrequencyType.map { it != Frequency.YEAR }.asLiveData()
    val isDecreaseFrequencyAvailable = taskFrequencyType.map { it != Frequency.DAY }.asLiveData()

    val saveTaskEvent = MutableLiveData<Event<Unit>>()
    val errorEvent = MutableLiveData<Event<String>>()

    init {
        commonTasks.value = getCommonTasks()
    }

    fun updateTaskFields(task: TaskItem.Task) {
        taskTitle.value = task.title
        taskPriority.value = task.priority.toFloat()
        taskFrequency.value = task.schedule.value.toString()
        taskFrequencyType.value = task.schedule.frequency
        taskProgress.value = task.progress
        taskNote.value = task.note
    }

    fun onSaveClick() {
        saveTaskEvent.postValue(Event(Unit))
    }

    fun onDecreaseFrequencyClick() {
        taskFrequencyType.value = taskFrequencyType.value.previous()
    }

    fun onIncreaseFrequencyClick() {
        taskFrequencyType.value = taskFrequencyType.value.next()
    }

    private fun getCommonTasks(): List<TaskItem.Task> {
        val out = mutableListOf<TaskItem.Task>()
        out.add(TaskItem.Task(1, "Крем для лица", 1, Schedule(1, Frequency.DAY), 20, "Описание задачи"))
        out.add(TaskItem.Task(2, "Массаж лица", 2, Schedule(4, Frequency.WEEK), 70))
        out.add(TaskItem.Task(3, "Пилинг для лица", 3, Schedule(2, Frequency.WEEK), 10))
        out.add(TaskItem.Task(4, "Депиляция ног", 2, Schedule(2, Frequency.MONTH), 40))
        out.add(getOtherTask())
        return out
    }

    private fun getOtherTask(): TaskItem.Task {
        return TaskItem.Task(0, "Другое", 2, Schedule(2, Frequency.WEEK), 50)
    }

}