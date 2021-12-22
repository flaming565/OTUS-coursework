package com.beautydiary.core.data.repositories

import com.beautydiary.core.data.common.BaseRepository
import com.beautydiary.core.data.common.toDomain
import com.beautydiary.core.data.common.toEntity
import com.beautydiary.core.data.models.TaskEntity
import com.beautydiary.core.data.room.dao.BaseTaskDao
import com.beautydiary.core.data.room.dao.TaskDao
import com.beautydiary.core.domain.interfaces.TaskRepository
import com.beautydiary.core.domain.models.DomainTask
import com.beautydiary.core.domain.models.DomainTaskAndCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.inject


internal class TaskRepositoryImpl : com.beautydiary.core.data.common.BaseRepository(), TaskRepository {
    private val dao by inject<TaskDao>()
    private val baseTaskDao by inject<BaseTaskDao>()

    override suspend fun getTask(id: Long): DomainTask {
        return dao.getById(id).toDomain()
    }

    override fun getAllTasks(): Flow<List<DomainTask>> {
        return dao.all().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getBaseTasks(baseCategoryId: Long): Flow<List<DomainTask>> {
        return baseTaskDao.getCategoryTasks(baseCategoryId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getCategoryTask(id: Long): Flow<DomainTaskAndCategory> {
        return dao.getByIdWithCategory(id).map { it.toDomain() }
    }

    override suspend fun createTask(task: DomainTask) {
        return dao.insert(
            TaskEntity(
                categoryId = task.categoryId,
                name = task.name,
                priority = task.priority.value,
                schedule = task.schedule.toEntity(),
                note = task.note ?: "",
                startDate = task.startDate
            )
        )
    }

    override suspend fun updateTask(task: DomainTask) {
        return dao.update(task.toEntity())
    }

    override suspend fun deleteTask(task: DomainTask) {
        return dao.delete(task.toEntity())
    }
}