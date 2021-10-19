package com.amalkina.beautydiary.data.repositories

import com.amalkina.beautydiary.data.common.BaseRepository
import com.amalkina.beautydiary.data.models.CategoryModel
import com.amalkina.beautydiary.data.room.dao.CategoryDao
import com.amalkina.beautydiary.domain.interfaces.CategoryRepository
import com.amalkina.beautydiary.domain.models.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.inject


internal class CategoryRepositoryImpl : BaseRepository(), CategoryRepository {
    private val dao by inject<CategoryDao>()

    override suspend fun getCategory(id: Long): Category {
        return dao.getCategory(id).toCategory()
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return dao.getAllCategories().map { list ->
            list.map {
                it.toCategory()
            }
        }
    }

    override suspend fun createCategory(category: Category) {
        return dao.insert(
            CategoryModel(
                name = category.name,
                imagePath = category.imagePath,
                drawableName = category.drawableName
            )
        )
    }

    override suspend fun updateCategory(category: Category) {
        return dao.update(category.toCategoryModel())
    }

    override suspend fun deleteCategory(category: Category) {
        return dao.delete(category.toCategoryModel())
    }

    private fun CategoryModel.toCategory() = Category(
        id = this.id,
        name = this.name,
        imagePath = this.imagePath,
        drawableName = this.drawableName,
        updateDate = this.updateDate,
        creationDate = this.creationDate
    )

    private fun Category.toCategoryModel() = CategoryModel(
        id = this.id,
        name = this.name,
        imagePath = this.imagePath,
        drawableName = this.drawableName,
        creationDate = this.creationDate
    )
}