package com.amalkina.beautydiary.ui.tasks.vm

import androidx.lifecycle.MutableLiveData
import com.amalkina.beautydiary.domain.common.Event
import com.amalkina.beautydiary.domain.models.Frequency
import com.amalkina.beautydiary.domain.models.FrequencyType
import com.amalkina.beautydiary.ui.common.vm.BaseViewModel
import com.amalkina.beautydiary.ui.tasks.models.TaskItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class TaskListViewModel : BaseViewModel() {

    private val _tasks = MutableStateFlow<List<Any>>(emptyList())
    val tasks: StateFlow<List<Any>> = _tasks

    val fragmentTitle = "Уход за телом"
    val addTaskEvent = MutableLiveData<Event<Unit>>()

    init {
        _tasks.value = getTasks()
    }

    fun onClickTask(id: Long) {

    }

    fun onClickNewTask() {
        addTaskEvent.postValue(Event(Unit))
    }

    private fun getTasks(): List<Any> {
        val out = mutableListOf<Any>()
        out.add(TaskItem.Task(1, "Крем для лица", 1, Frequency(1, FrequencyType.DAY), 20, "Описание задачи"))
        out.add(TaskItem.Task(2, "Массаж лица", 2, Frequency(4, FrequencyType.WEEK), 70))
        out.add(TaskItem.Task(3, "Пилинг для лица", 3, Frequency(2, FrequencyType.WEEK), 10))
        out.add(TaskItem.Task(4, "Депиляция ног", 2, Frequency(2, FrequencyType.MONTH), 40))
        out.add(TaskItem.New)
        return out
    }

}