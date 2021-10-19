package com.amalkina.beautydiary.ui.home.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amalkina.beautydiary.domain.common.Event
import com.amalkina.beautydiary.domain.common.Result
import com.amalkina.beautydiary.domain.models.Category
import com.amalkina.beautydiary.domain.usecases.category.GetCategoriesUseCase
import com.amalkina.beautydiary.domain.usecases.category.UpdateCategoryUseCase
import com.amalkina.beautydiary.ui.common.ext.toCategory
import com.amalkina.beautydiary.ui.common.ext.toHomeCategory
import com.amalkina.beautydiary.ui.common.ext.tryCast
import com.amalkina.beautydiary.ui.common.vm.BaseViewModel
import com.amalkina.beautydiary.ui.home.models.HomeCategory
import com.amalkina.beautydiary.ui.home.models.HomeCategoryNew
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject

// TODO: use loading state
internal class HomeViewModel : BaseViewModel() {
    private val getCategoriesUseCase by inject<GetCategoriesUseCase>()
    private val updateCategoryUseCase by inject<UpdateCategoryUseCase>()

    val categories: StateFlow<List<HomeCategory>> = getCategoriesUseCase.get().run {
        mapGetCategoriesResult(this)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )
    var selectedCategory: HomeCategory? = null

    val userActionEvent = MutableLiveData<Event<UserActionItem>>()

    fun onClickCategory(id: Long) {
        userActionEvent.postValue(Event(UserActionItem.OnClickCategory(id)))
    }

    fun onLongClickCategory(id: Long): Boolean {
        selectedCategory = categories.value.firstOrNull { it.id == id }
        userActionEvent.postValue(Event(UserActionItem.OnLongClickCategory))
        return true
    }

    fun onEditCategory() {
        userActionEvent.postValue(Event(UserActionItem.EditCategory))
    }

    fun onDeleteCategory() {
        userActionEvent.postValue(Event(UserActionItem.DeleteCategory))
    }

    fun deleteSelectedCategory() {
        selectedCategory?.let { deleteCategory(it.toCategory()) }
    }

    fun onClickNewCategory() {
        userActionEvent.postValue(Event(UserActionItem.AddCategory))
    }

    private fun mapGetCategoriesResult(result: Result): Flow<List<HomeCategory>> {
        var categories = flowOf(emptyList<HomeCategory>())
        when (result) {
            is Result.Success<*> -> {
                tryCast<Flow<List<Category>>>(result.value) {
                    categories = this.transform { list ->
                        emit(list.map { it.toHomeCategory() } + listOf(HomeCategoryNew))
                    }
                }
            }
            is Result.Error -> {
                errorEvent.postValue(Event(result.message))
            }
        }
        return categories
    }

    private fun deleteCategory(category: Category) {
        isLoading.value = true
        launch {
            updateCategoryUseCase.delete(category).let {
                isLoading.value = false
                if (it is Result.Error) {
                    errorEvent.postValue(Event(it.message))
                }
            }
        }
    }

    sealed class UserActionItem {
        class OnClickCategory(val id: Long) : UserActionItem()
        object OnLongClickCategory : UserActionItem()
        object AddCategory : UserActionItem()
        object DeleteCategory : UserActionItem()
        object EditCategory : UserActionItem()
    }
}