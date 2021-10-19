package com.amalkina.beautydiary.ui.home.vm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.domain.common.Event
import com.amalkina.beautydiary.domain.common.Result
import com.amalkina.beautydiary.domain.models.Category
import com.amalkina.beautydiary.domain.usecases.category.GetCategoryUseCase
import com.amalkina.beautydiary.domain.usecases.category.UpdateCategoryUseCase
import com.amalkina.beautydiary.domain.usecases.common.ReadWriteImageUseCase
import com.amalkina.beautydiary.ui.common.ext.cast
import com.amalkina.beautydiary.ui.common.ext.getDrawableRes
import com.amalkina.beautydiary.ui.common.ext.resNameById
import com.amalkina.beautydiary.ui.common.ext.toHomeCategory
import com.amalkina.beautydiary.ui.common.models.BaseModel.Companion.getString
import com.amalkina.beautydiary.ui.common.vm.BaseViewModel
import com.amalkina.beautydiary.ui.home.models.BaseCategory
import com.amalkina.beautydiary.ui.home.models.getBaseCategories
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.component.inject


internal class AddCategoryViewModel(categoryId: Long = 0) : BaseViewModel() {

    private val readWriteImageUseCase by inject<ReadWriteImageUseCase>()
    private val getCategoryUseCase by inject<GetCategoryUseCase>()
    private val updateCategoryUseCase by inject<UpdateCategoryUseCase>()

    val isEditMode = categoryId > 0
    val fragmentTitle = getString(
        when (isEditMode) {
            true -> R.string.add_category_edit_title
            else -> R.string.add_category_title
        }
    )

    init {
        if (isEditMode)
            loadEditableCategory(categoryId)
    }

    private val categories = getBaseCategories()
    val categoryNames = getBaseCategories().map { category -> category.name }

    val currentIndex = MutableStateFlow(-1)
    private val selectedCategory = currentIndex.transform {
        categories.getOrNull(it)?.let { category ->
            selectedCategoryTitle.value = category.name
            selectedCategoryDrawable.value = category.drawable
            emit(category)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = BaseCategory.OtherCategory
    )

    private var selectedCategoryId = 0L
    val selectedCategoryTitle = MutableStateFlow("")
    val selectedCategoryDrawable = MutableStateFlow<Drawable?>(null)
    val selectedCategoryBitmap = MutableStateFlow<Bitmap?>(null)

    val userActionEvent = MutableLiveData<Event<UserActionItem>>()
    val launchActionEvent = MutableLiveData<Event<LaunchItem>>()

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

    fun onSaveClick() {
        saveCategory(createCategory())
    }

    fun onChangeImageClick() {
        userActionEvent.postValue(Event(UserActionItem.CHANGE_IMAGE))
    }

    fun onClickCamera() {
        userActionEvent.postValue(Event(UserActionItem.SELECT_CAMERA))
    }

    fun onClickGallery() {
        userActionEvent.postValue(Event(UserActionItem.SELECT_GALLERY))
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
            selectedCategoryDrawable.value = get<Context>().getDrawableRes(R.drawable.common_category_other)
        }
    }

    fun getTempUri(): Uri? {
        tempImageUri = readWriteImageUseCase.getTempUri()
        return tempImageUri ?: run {
            errorEvent.postValue(Event(ErrorItem.TEMP_FILE.message))
            null
        }
    }

    private fun loadEditableCategory(categoryId: Long) {
        launch {
            val category = getCategoryUseCase.get(categoryId)
                .run { mapGetCategoryResult(this) }
            category?.toHomeCategory()?.let {
                selectedCategoryId = it.id
                selectedCategoryTitle.value = it.name
                selectedCategoryDrawable.value = it.drawable
                selectedCategoryBitmap.value = it.bitmap
            }
        }
    }

    private fun createCategory(): Category {
        val newImagePath = readyImagePathEvent.value
        val currentImagePath = selectedCategory.value.imagePath
        val imagePath = newImagePath ?: currentImagePath
        val drawableName = selectedCategory.value.imageDrawable?.let { get<Context>().resNameById(it) }

        return Category(
            id = selectedCategoryId,
            name = selectedCategoryTitle.value,
            imagePath = imagePath,
            drawableName = if (imagePath.isNullOrBlank()) drawableName else null
        )
    }

    private fun saveCategory(category: Category) {
        isLoading.value = true
        launch {
            val result = if (isEditMode) updateCategoryUseCase.update(category)
            else updateCategoryUseCase.create(category)
            result.let {
                isLoading.value = false
                mapUpdateCategoryResult(it)
            }
        }
    }

    private fun mapGetCategoryResult(result: Result): Category? {
        return mapResponseResult(result, ErrorItem.DATABASE.message)?.let { cast<Category>(it) }
    }

    private fun mapUpdateCategoryResult(result: Result) {
        mapResponseResult(result, ErrorItem.DATABASE.message) {
            userActionEvent.postValue(Event(UserActionItem.UPDATE_CATEGORY))
        }
    }

    private fun mapResponseResult(
        result: Result,
        errorMessage: String,
        onSuccess: (() -> Unit)? = null
    ): Any? {
        return when (result) {
            is Result.Success<*> -> {
                onSuccess?.invoke()
                result.value
            }
            is Result.Error -> {
                errorEvent.postValue(Event(errorMessage))
                null
            }
        }
    }

    private fun mapUriState(result: Result?): String? {
        return result?.let {
            mapResponseResult(result, ErrorItem.SAVE.message)?.let { cast<String>(it) }
        }
    }

    enum class UserActionItem {
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