package com.beautydiary.home.vm

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.beautydiary.core_ui.ext.*
import com.beautydiary.core_ui.models.BaseModel.Companion.getString
import com.beautydiary.core_ui.utils.Event
import com.beautydiary.core_ui.vm.BaseViewModel
import com.beautydiary.domain.common.Result
import com.beautydiary.domain.models.DomainCategory
import com.beautydiary.domain.usecases.CategoryActionsUseCase
import com.beautydiary.domain.usecases.ReadWriteImageUseCase
import com.beautydiary.home.R
import com.beautydiary.home.ext.toUIModel
import com.beautydiary.home.models.HomeCategory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.component.inject


internal class AddCategoryViewModel(categoryId: Long = 0) : BaseViewModel() {

    private val readWriteImageUseCase by inject<ReadWriteImageUseCase>()
    private val categoryUseCase by inject<CategoryActionsUseCase>()

    val userActionEvent = MutableLiveData<Event<UserAction>>()
    val launchActionEvent = MutableLiveData<Event<LaunchItem>>()

    val isEditMode = categoryId > 0
    val fragmentTitle = getString(
        when (isEditMode) {
            true -> R.string.common_edit
            else -> R.string.add_category_title
        }
    )

    init {
        if (isEditMode)
            loadEditableCategory(categoryId)
    }

    private val baseCategories: StateFlow<List<HomeCategory>> = categoryUseCase.baseCategories()
        .flatMapMerge { mapGetCategoriesResult(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    val categoryNames =
        baseCategories.map(viewModelScope) { list -> list.map { category -> category.name } }

    val currentIndex = MutableStateFlow(-1)
    private val selectedCategory =
        currentIndex.mapToMutable(viewModelScope) { baseCategories.value.getOrNull(it) }
    val selectedCategoryTitle = selectedCategory.mapToMutable(viewModelScope) { it?.name ?: "" }
    val selectedCategoryDrawable = selectedCategory.mapToMutable(viewModelScope) { it?.drawable }
    val selectedCategoryBitmap = selectedCategory.mapToMutable(viewModelScope) { it?.bitmap }

    private var tempImageUri: Uri? = null
    private val rawImageUri = MutableStateFlow<Uri?>(null)
    val readyImagePathEvent: StateFlow<String?> = rawImageUri
        .map { readWriteImageUseCase.compressUriImage(it) }
        .map { mapUriState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    private val isCategoryChanged =
        combine(
            selectedCategory,
            selectedCategoryTitle,
            selectedCategoryBitmap
        ) { category, title, image ->
            category?.name != title || category.bitmap != image
        }
    val isSaveButtonEnabled =
        combine(
            selectedCategoryTitle,
            isCategoryChanged,
            isLoading.asFlow()
        ) { title, isChanged, isLoading ->
            title.isNotEmpty() && (isChanged || !isEditMode) && !isLoading
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = false
        )

    fun onSaveClick() {
        saveCategory(createCategory())
    }

    fun onChangeImageClick() {
        userActionEvent.postValue(Event(UserAction.CHANGE_IMAGE))
    }

    fun onCameraClick() {
        userActionEvent.postValue(Event(UserAction.SELECT_CAMERA))
    }

    fun onGalleryClick() {
        userActionEvent.postValue(Event(UserAction.SELECT_GALLERY))
    }

    fun getImageFromCamera() {
        launchActionEvent.postValue(Event(LaunchItem.LAUNCH_CAMERA))
    }

    fun getImageFromGallery() {
        launchActionEvent.postValue(Event(LaunchItem.LAUNCH_GALLERY))
    }

    fun notifyReadyImageFromCamera(isSaved: Boolean) {
        if (isSaved) tempImageUri?.let { rawImageUri.value = it }
    }

    fun notifyReadyImageFromGallery(uri: Uri) {
        rawImageUri.value = uri
    }

    fun updateCategoryImage(imagePath: String) {
        readWriteImageUseCase.getBitmapFromPath(imagePath)?.let {
            selectedCategoryBitmap.value = it
        } ?: run {
            // TODO: get other image^ show error
            selectedCategoryDrawable.value = get<Context>().getDrawableRes(R.drawable.base_image)
        }
    }

    fun getTempUri(): Uri? {
        tempImageUri = readWriteImageUseCase.getTempUri()
        if (tempImageUri == null)
            errorEvent.postValue(Event(ErrorItem.TEMP_FILE.message))
        return tempImageUri
    }

    private fun loadEditableCategory(categoryId: Long) {
        isLoading.value = true
        launch {
            val result = categoryUseCase.get(categoryId)
            val category = mapGetCategoryResult(result)
            category?.toUIModel()?.let {
                selectedCategory.value = it
            }
        }
    }

    private fun createCategory(): DomainCategory {
        val newImagePath = readyImagePathEvent.value
        val currentImagePath = selectedCategory.value?.imagePath
        val imagePath = newImagePath ?: currentImagePath
        val drawableName =
            selectedCategory.value?.imageDrawable?.let { get<Context>().resNameById(it) }

        return DomainCategory(
            id = selectedCategory.value?.id ?: 0,
            baseCategoryId = selectedCategory.value?.baseCategoryId ?: 0,
            name = selectedCategoryTitle.value.trim(),
            imagePath = imagePath,
            drawableName = if (imagePath.isNullOrBlank()) drawableName else null
        )
    }

    private fun saveCategory(category: DomainCategory) {
        launch {
            val result = if (isEditMode) categoryUseCase.update(category)
            else categoryUseCase.create(category)
            mapUpdateCategoryResult(result)
        }
    }

    private fun mapGetCategoriesResult(result: Result): Flow<List<HomeCategory>> {
        var categories: Flow<List<HomeCategory>> = flowOf(emptyList())
        mapResponseResult(result)?.let {
            tryCast<Flow<List<DomainCategory>>>(it) {
                categories = this.transform { list ->
                    emit(list.map { category -> category.toUIModel() })
                }
            }
        }
        return categories
    }

    private fun mapGetCategoryResult(result: Result): DomainCategory? {
        return mapResponseResult(result)?.let {
            cast<DomainCategory>(it)
        }
    }

    private fun mapUpdateCategoryResult(result: Result) {
        mapResponseResult(result, ErrorItem.DATABASE.message) {
            userActionEvent.postValue(Event(UserAction.UPDATE_CATEGORY))
        }
    }

    private fun mapUriState(result: Result?): String? {
        return result?.let {
            mapResponseResult(result, ErrorItem.SAVE.message)?.let { cast<String>(it) }
        }
    }

    enum class UserAction {
        CHANGE_IMAGE, SELECT_CAMERA, SELECT_GALLERY, UPDATE_CATEGORY
    }

    enum class LaunchItem {
        LAUNCH_CAMERA, LAUNCH_GALLERY
    }

    enum class ErrorItem(val message: String) {
        SAVE(getString(R.string.add_category_error_saving)),
        TEMP_FILE(getString(R.string.add_category_get_temp_uri_error)),
        DATABASE(getString(R.string.add_category_error_database))
    }
}