package com.amalkina.beautydiary.domain.usecases

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.util.*

@KoinApiExtension
class ReadWriteImageUseCase(context: Context) : BaseUseCase() {

    private val weakContext = WeakReference(context)
    private val directory = getFilesDir(context)
    private val tempDirectory = getCacheDir(context)

    fun getTempUri(): Uri? {
        val file = File(tempDirectory, "$TEMP_FILE${System.currentTimeMillis()}$IMAGE_EXTENSION")
        return try {
            FileProvider.getUriForFile(weakContext.get()!!, AUTHORITY, file)
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }
    }

    fun getFilesUri(path: String): Uri = Uri.fromFile(File(path))

    suspend fun compressUriImage(uri: Uri?): State? {
        val result = async(Dispatchers.IO) {
            uri?.let {
                val fileName = getFileName()
                val fileSize = getSizeFromUri(uri)
                val file = File(directory, "$fileName$IMAGE_EXTENSION")

                val outputStream = FileOutputStream(file)
                val inputStream = if (fileSize > MAX_IMAGE_SIZE)
                    getCompressedOutputStream(weakContext.get()!!, uri)
                else
                    weakContext.get()!!.contentResolver.openInputStream(uri)!!

                try {
                    val buffer = ByteArray(1024)
                    var length: Int
                    while ((inputStream.read(buffer).also { length = it }) > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                    return@async State.Ok(file.absolutePath)
                } catch (ex: Exception) {
                    Timber.e(ex)
                    return@async State.Error
                } finally {
                    inputStream.close()
                    outputStream.close()
                }
            }
            return@async null
        }
        return result.await()
    }

    private fun getSizeFromUri(uri: Uri): Long {
        weakContext.get()?.contentResolver
            ?.query(uri, null, null, null, null)
            ?.use { cursor ->
                cursor.moveToPosition(0)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                return cursor.getLong(sizeIndex)
            }
        return 0L
    }

    private fun getFilesDir(context: Context): File {
        return File(context.filesDir, IMAGES_FOLDER).apply {
            this.mkdirs()
        }
    }

    private fun getCacheDir(context: Context): File {
        return File(context.cacheDir, TEMP_IMAGES_FOLDER).apply {
            this.mkdirs()
        }
    }

    private fun getFileName(): String = UUID.randomUUID().toString()

    private fun getCompressedOutputStream(context: Context, uri: Uri): ByteArrayInputStream {
        val file = context.contentResolver.openFileDescriptor(uri, "r")
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = 3

        val bitmap = BitmapFactory.decodeFileDescriptor(file?.fileDescriptor, null, bitmapOptions)

        val baos = ByteArrayOutputStream()
        var currSize: Int
        var quality = 100
        do {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
            currSize = baos.toByteArray().size
            quality -= 10

        } while (currSize >= MAX_IMAGE_SIZE && quality != 0)
        return ByteArrayInputStream(baos.toByteArray())
    }

    sealed class State {
        data class Ok(val filePath: String) : State()
        object Error : State()
    }

    private companion object {
        private const val IMAGES_FOLDER = "images"
        private const val TEMP_IMAGES_FOLDER = "temp_images"
        private const val TEMP_FILE = "temp_file"
        private const val IMAGE_EXTENSION = ".jpeg"
        private const val AUTHORITY = "com.amalkina.beautydiary.fileprovider"
        private const val MAX_IMAGE_SIZE = 5 * 1024 * 1024
    }
}
