package com.amalkina.beautydiary.ui.home.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amalkina.beautydiary.domain.common.Event
import com.amalkina.beautydiary.domain.common.Result
import com.amalkina.beautydiary.domain.models.DomainCategory
import com.amalkina.beautydiary.domain.models.DomainCategoryWithTasks
import com.amalkina.beautydiary.domain.usecases.CategoryActionsUseCase
import com.amalkina.beautydiary.ui.common.ext.mapToMutable
import com.amalkina.beautydiary.ui.common.ext.toDomain
import com.amalkina.beautydiary.ui.common.ext.toUIModel
import com.amalkina.beautydiary.ui.common.ext.tryCast
import com.amalkina.beautydiary.ui.common.vm.BaseViewModel
import com.amalkina.beautydiary.ui.home.models.HomeCategory
import com.amalkina.beautydiary.ui.home.models.HomeCategoryNew
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject


internal class HomeViewModel : BaseViewModel() {
    private val categoryUseCase by inject<CategoryActionsUseCase>()

    val isFragmentLoading = isLoading.mapToMutable(viewModelScope) { it }
    val userActionEvent = MutableLiveData<Event<UserAction>>()

    val categories: StateFlow<List<HomeCategory>> = categoryUseCase.allWithTasks()
        .flatMapMerge { mapGetCategoriesResult(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    var selectedCategory: DomainCategory? = null

    fun onClickCategory(id: Long) {
        initSelectedCategory(id)
        userActionEvent.postValue(Event(UserAction.ON_CLICK))
    }

    fun onLongClickCategory(id: Long): Boolean {
        initSelectedCategory(id)
        userActionEvent.postValue(Event(UserAction.ON_LONG_CLICK))
        return true
    }

    fun onAddCategory() {
        userActionEvent.postValue(Event(UserAction.ADD_CATEGORY))
    }

    fun onEditCategory() {
        userActionEvent.postValue(Event(UserAction.EDIT_CATEGORY))
    }

    fun onDeleteCategory() {
        userActionEvent.postValue(Event(UserAction.DELETE_CATEGORY))
    }

    fun deleteSelectedCategory() {
        selectedCategory?.let { deleteCategory(it) }
    }

    private fun mapGetCategoriesResult(result: Result): Flow<List<HomeCategory>> {
        var categories: Flow<List<HomeCategory>> = flowOf(emptyList())
        mapResponseResult(result)?.let {
            tryCast<Flow<List<DomainCategoryWithTasks>>>(it) {
                categories = this.transform { list ->
                    emit(list.map { category -> category.toUIModel() } + listOf(HomeCategoryNew))
                }
            }
        }
        return categories
    }

    private fun initSelectedCategory(id: Long) {
        selectedCategory = categories.value.firstOrNull { it.id == id }?.toDomain()
    }

    private fun deleteCategory(category: DomainCategory) {
        isFragmentLoading.value = true
        launch {
            val result = categoryUseCase.delete(category)
            mapResponseResult(result)
        }
    }

    enum class UserAction {
        ON_CLICK,
        ON_LONG_CLICK,
        ADD_CATEGORY,
        EDIT_CATEGORY,
        DELETE_CATEGORY
    }
}