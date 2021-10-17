package com.amalkina.beautydiary.data.repositories

import com.amalkina.beautydiary.data.common.BaseRepository
import com.amalkina.beautydiary.data.models.TaskFrequency
import com.amalkina.beautydiary.data.models.TaskModel
import com.amalkina.beautydiary.data.models.TaskSchedule
import com.amalkina.beautydiary.data.room.dao.TaskDao
import com.amalkina.beautydiary.domain.interfaces.TaskRepository
import com.amalkina.beautydiary.domain.models.Frequency
import com.amalkina.beautydiary.domain.models.Priority
import com.amalkina.beautydiary.domain.models.Schedule
import com.amalkina.beautydiary.domain.models.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.inject


internal class TaskRepositoryImpl : BaseRepository(), TaskRepository {
    private val dao by inject<TaskDao>()

    override fun getTask(id: Long): Flow<Task> {
        return dao.getTask(id).map {
            it.toTask()
        }
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return dao.getAllTasks().map { list ->
            list.map {
                it.toTask()
            }
        }
    }

    override fun getCategoryTasks(categoryId: Long): Flow<List<Task>> {
        return dao.getCategoryTasks(categoryId).map { list ->
            list.map {
                it.toTask()
            }
        }
    }

    override suspend fun createTask(task: Task) {
        return dao.insert(
            TaskModel(
                categoryId = task.categoryId,
                name = task.name,
                priority = task.priority.value,
                schedule = task.schedule.toTaskSchedule(),
                note = task.note,
                startDate = task.startDate
            )
        )
    }

    override suspend fun updateTask(task: Task) {
        return dao.update(task.toTaskModel())
    }

    override suspend fun deleteTask(task: Task) {
        return dao.delete(task.toTaskModel())
    }

    private fun TaskModel.toTask() = Task(
        id = this.id,
        categoryId = this.categoryId,
        name = this.name,
        priority = Priority.fromInt(this.priority),
        schedule = this.schedule.toSchedule(),
        note = this.note,
        startDate = this.startDate,
        updateDate = this.updateDate,
        creationDate = this.creationDate
    )

    private fun Task.toTaskModel() = TaskModel(
        id = this.id,
        categoryId = this.categoryId,
        name = this.name,
        priority = this.priority.value,
        schedule = this.schedule.toTaskSchedule(),
        note = this.note,
        startDate = this.startDate,
        creationDate = this.creationDate
    )

    private fun TaskSchedule.toSchedule() = Schedule(
        value = this.value,
        frequency = Frequency.values()[this.frequency.ordinal]
    )

    private fun Schedule.toTaskSchedule() = TaskSchedule(
        value = this.value,
        frequency = TaskFrequency.values()[this.frequency.ordinal]
    )
}