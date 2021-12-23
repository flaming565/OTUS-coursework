package com.beautydiary.core.ui.home.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beautydiary.core.domain.common.ApplicationSettings
import com.beautydiary.core.ui.common.utils.Event
import com.beautydiary.domain.models.DomainCategoryWithTasks
import com.beautydiary.domain.models.DomainQuote
import com.beautydiary.core.domain.usecases.CategoryActionsUseCase
import com.beautydiary.core.domain.usecases.GetQuoteUseCase
import com.beautydiary.core.ui.common.ext.cast
import com.beautydiary.core.ui.common.ext.toDomain
import com.beautydiary.core.ui.common.ext.tryCast
import com.beautydiary.core.domain.common.Result
import com.beautydiary.core.ui.common.ext.toUIModel
import com.beautydiary.core.ui.common.vm.BaseViewModel
import com.beautydiary.core.ui.home.models.CategoryItem
import com.beautydiary.core.ui.home.models.HomeCategory
import com.beautydiary.core.ui.home.models.HomeCategoryNew
import com.beautydiary.core.ui.home.models.QuoteModel
import com.beautydiary.domain.models.DomainCategory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject


internal class HomeViewModel : BaseViewModel() {
    private val categoryUseCase by inject<CategoryActionsUseCase>()
    private val getQuoteUseCase by inject<GetQuoteUseCase>()
    private val applicationSettings by inject<ApplicationSettings>()

    val userActionEvent = MutableLiveData<Event<UserAction>>()

    val categories: StateFlow<List<CategoryItem>> = categoryUseCase.allWithTasks()
        .flatMapMerge { mapGetCategoriesResult(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    var selectedCategory: DomainCategory? = null
    val quote = MutableLiveData<QuoteModel>()

    init {
        if (applicationSettings.shouldQuoteBeShown)
            loadQuote()
    }

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

    private fun mapGetCategoriesResult(result: Result): Flow<List<CategoryItem>> {
        var categories: Flow<List<CategoryItem>> = flowOf(emptyList())
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
        selectedCategory =
            (categories.value.firstOrNull { it.id == id } as? HomeCategory)?.toDomain()
    }

    private fun deleteCategory(category: DomainCategory) {
        launch {
            val result = categoryUseCase.delete(category)
            mapResponseResult(result)
        }
    }

    private fun loadQuote() {
        launch {
            val result = getQuoteUseCase.randomQuote()
            mapResponseResult(result)?.let {
                quote.value = cast<DomainQuote>(it)?.toUIModel()
            }
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