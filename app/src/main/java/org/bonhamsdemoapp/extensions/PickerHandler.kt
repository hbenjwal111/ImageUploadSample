package org.bonhamsdemoapp.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

object PickerHandler {

    private const val MIME_TYPE_IMAGE: String = "image/*"

    /**
     * Get image from gallery intent
     *
     * @return
     */
    fun getImageFromGalleryIntent(): Intent {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = MIME_TYPE_IMAGE
        return galleryIntent
    }

    /**
     * Take picture from camera intent
     *
     * @param context
     * @return
     */
    fun takePictureFromCameraIntent(context: Context): Pair<Intent, File?> {
        val imageFile: File?
        var imageUri: Uri?

        try {
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            imageFile =
                File.createTempFile("${System.currentTimeMillis()}_", ".jpg", storageDir)

            imageFile?.let { file ->
                imageUri = FileProvider.getUriForFile(
                    context,
                    "org.bonhamsdemoapp.fileprovider",
                    file
                )

                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

                return Pair(cameraIntent, imageFile)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle file creation error
        }

        return Pair(Intent(), null)
    }

    /**
     * Resolve file path from uri
     *
     * @param uri
     * @param context
     * @return
     */
    fun resolveFilePathFromUri(uri: Uri, context: Context): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return ""
    }

    /**
     * Create profile image
     *
     * @param imageUri
     * @return
     */
    fun createProfileImage(imageUri: String?): MultipartBody.Part {
        val imageFile = File(imageUri ?: "")
        val requestFile = imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
    }

    /**
     * Get image name from path
     *
     * @param filePath
     * @return
     */
    fun getImageNameFromPath(filePath: String): String {
        val file = File(filePath)
        return file.name // This retrieves the file name
    }

    /**
     * Get image uri
     *
     * @param filePath
     * @return
     */
    fun getImageUri(filePath: String): Uri? {
        val file = File(filePath)
        return Uri.fromFile(file)
    }
}
