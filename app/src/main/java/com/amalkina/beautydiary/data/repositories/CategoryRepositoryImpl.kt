package com.amalkina.beautydiary.data.repositories

import com.amalkina.beautydiary.data.common.BaseRepository
import com.amalkina.beautydiary.data.common.toDomain
import com.amalkina.beautydiary.data.common.toEntity
import com.amalkina.beautydiary.data.models.CategoryEntity
import com.amalkina.beautydiary.data.room.dao.BaseCategoryDao
import com.amalkina.beautydiary.data.room.dao.CategoryDao
import com.amalkina.beautydiary.domain.interfaces.CategoryRepository
import com.amalkina.beautydiary.domain.models.DomainCategory
import com.amalkina.beautydiary.domain.models.DomainCategoryWithTasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.inject


internal class CategoryRepositoryImpl : BaseRepository(), CategoryRepository {
    private val dao by inject<CategoryDao>()
    private val baseCategoryDao by inject<BaseCategoryDao>()

    override suspend fun getCategory(id: Long): DomainCategory {
        return dao.getById(id).toDomain()
    }

    override fun getCategoryWithTasks(id: Long): Flow<DomainCategoryWithTasks> {
        return dao.getByIdWithTasks(id).map { it.toDomain() }
    }

    override fun getAllCategories(): Flow<List<DomainCategory>> {
        return dao.all().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllCategoriesWithTasks(): Flow<List<DomainCategoryWithTasks>> {
        return dao.allWithTasks().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getBaseCategories(): Flow<List<DomainCategory>> {
        return baseCategoryDao.getAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun createCategory(category: DomainCategory) {
        return dao.insert(
            CategoryEntity(
                baseCategoryId = category.baseCategoryId,
                name = category.name,
                imagePath = category.imagePath,
                drawableName = category.drawableName
            )
        )
    }

    override suspend fun updateCategory(category: DomainCategory) {
        return dao.update(category.toEntity())
    }

    override suspend fun deleteCategory(category: DomainCategory) {
        return dao.delete(category.toEntity())
    }


}