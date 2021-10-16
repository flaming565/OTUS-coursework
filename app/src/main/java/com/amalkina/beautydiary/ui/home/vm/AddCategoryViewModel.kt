package com.amalkina.beautydiary.ui.home.vm

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.domain.common.Event
import com.amalkina.beautydiary.domain.models.Category
import com.amalkina.beautydiary.domain.usecases.ReadWriteImageUseCase
import com.amalkina.beautydiary.domain.usecases.UpdateCategoryUseCase
import com.amalkina.beautydiary.ui.common.ext.getDrawableRes
import com.amalkina.beautydiary.ui.common.ext.resNameById
import com.amalkina.beautydiary.ui.common.vm.BaseViewModel
import com.amalkina.beautydiary.ui.home.models.BaseCategory
import com.amalkina.beautydiary.ui.home.models.getBaseCategories
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.get
import org.koin.core.component.inject
import timber.log.Timber
import java.io.FileNotFoundException

@KoinApiExtension
internal class AddCategoryViewModel(categoryId: Long) : BaseViewModel() {

    private val readWriteImageUseCase by inject<ReadWriteImageUseCase>()
    private val updateCategoryUseCase by inject<UpdateCategoryUseCase>()

    val isEditMode = categoryId >= 0

    private val categories = getBaseCategories()
    val categoryNames = getBaseCategories().map { category -> category.name }

    val currentIndex = MutableStateFlow(-1)
    private val selectedCategory = currentIndex.transform {
        categories.getOrNull(it)?.let { category ->
            selectedCategoryTitle.value = category.name
            selectedCategoryImage.value = category.drawable
            emit(category)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = BaseCategory.OtherCategory
    )
    val selectedCategoryTitle = MutableStateFlow("")
    val selectedCategoryImage = MutableStateFlow<Drawable?>(null)

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

    val isLoading = MutableStateFlow(false)
    val errorEvent = MutableLiveData<Event<ErrorItem>>()

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

    fun updateCategoryImage(context: Context, imagePath: String) {
        val uri = readWriteImageUseCase.getFilesUri(imagePath)
        val drawable = try {
            val inputStream = context.contentResolver.openInputStream(uri)
            Drawable.createFromStream(inputStream, uri.toString())
        } catch (e: FileNotFoundException) {
            // TODO: gte other image
            get<Context>().getDrawableRes(R.drawable.common_category_other)
        }
        drawable?.let { selectedCategoryImage.value = it }
    }

    fun getTempUri(): Uri? {
        tempImageUri = readWriteImageUseCase.getTempUri()
        return tempImageUri ?: run {
            errorEvent.postValue(Event(ErrorItem.TEMP_FILE))
            null
        }
    }

    private fun createCategory(): Category {
        val newImagePath = readyImagePathEvent.value
        val currentImagePath = selectedCategory.value.imagePath
        val imagePath = newImagePath ?: currentImagePath
        val drawableName = selectedCategory.value.imageDrawable?.let { get<Context>().resNameById(it) }

        return Category(
            name = selectedCategoryTitle.value,
            imagePath = imagePath,
            drawableName = if (imagePath.isNullOrBlank()) drawableName else null
        )
    }

    private fun saveCategory(category: Category) {
        isLoading.value = true
        launch {
            updateCategoryUseCase.create(category).let {
                isLoading.value = false
                mapUpdateCategoryResult(it)
            }
        }
    }

    private fun mapUpdateCategoryResult(result: UpdateCategoryUseCase.Result) {
        when (result) {
            is UpdateCategoryUseCase.Result.Error -> {
                Timber.d("Category update error: ${result.error}")
                errorEvent.postValue(Event(ErrorItem.DATABASE))
            }
            else -> userActionEvent.postValue(Event(UserActionItem.UPDATE_CATEGORY))
        }
    }

    private fun mapUriState(result: ReadWriteImageUseCase.State?): String? {
        return when (result) {
            is ReadWriteImageUseCase.State.Ok -> result.filePath
            is ReadWriteImageUseCase.State.Error -> {
                errorEvent.postValue(Event(ErrorItem.SAVE))
                null
            }
            else -> null
        }
    }


    enum class UserActionItem {
        CHANGE_IMAGE, SELECT_CAMERA, SELECT_GALLERY, UPDATE_CATEGORY
    }

    enum class LaunchItem {
        LAUNCH_CAMERA, LAUNCH_GALLERY
    }

    enum class ErrorItem {
        SAVE, TEMP_FILE, DATABASE
    }
}