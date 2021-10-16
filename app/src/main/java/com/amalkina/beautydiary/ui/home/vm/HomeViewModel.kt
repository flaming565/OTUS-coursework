package com.amalkina.beautydiary.ui.home.vm

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amalkina.beautydiary.domain.common.Event
import com.amalkina.beautydiary.domain.models.Category
import com.amalkina.beautydiary.domain.usecases.GetCategoriesUseCase
import com.amalkina.beautydiary.ui.common.ext.resIdByName
import com.amalkina.beautydiary.ui.common.vm.BaseViewModel
import com.amalkina.beautydiary.ui.home.models.HomeCategory
import com.amalkina.beautydiary.ui.home.models.HomeCategoryNew
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.get
import org.koin.core.component.inject

@KoinApiExtension
internal class HomeViewModel : BaseViewModel() {
    private val getCategoriesUseCase by inject<GetCategoriesUseCase>()

    val categories: StateFlow<List<HomeCategory>> = getCategoriesUseCase.get().run {
        mapGetCategoriesResult(this)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    val onClickCategoryEvent = MutableLiveData<Event<Long>>()
    val addCategoryEvent = MutableLiveData<Event<Unit>>()

    fun onClickCategory(id: Long) {
        onClickCategoryEvent.postValue(Event(id))
    }

    fun onClickNewCategory() {
        addCategoryEvent.postValue(Event(Unit))
    }

    private fun mapGetCategoriesResult(result: GetCategoriesUseCase.Result): Flow<List<HomeCategory>> {
        return when (result) {
            is GetCategoriesUseCase.Result.Ok -> {
                result.response.transform { list ->
                    emit(list.map { it.toHomeCategory() } + listOf(HomeCategoryNew))
                }
            }
            else -> {
                // TODO: error processing
                flow { emit(listOf(HomeCategoryNew)) }
            }
        }
    }

    private fun Category.toHomeCategory() = HomeCategory(
        id = this.id,
        name = this.name,
        imagePath = this.imagePath,
        imageDrawable = this.drawableName?.let { get<Context>().resIdByName(it, "drawable") }
    )

}