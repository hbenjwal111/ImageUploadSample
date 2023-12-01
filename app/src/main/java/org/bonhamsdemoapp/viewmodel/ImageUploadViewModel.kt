package org.bonhamsdemoapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.bonhamsdemoapp.api.NetworkResult
import org.bonhamsdemoapp.model.ImageUploadResponse
import org.bonhamsdemoapp.repo.ImageRepository
import javax.inject.Inject

/**
 * Image upload view model
 *
 * @property repository
 * @constructor Create Image upload view model
 */
@HiltViewModel
class ImageUploadViewModel @Inject constructor(private val repository: ImageRepository) :
    ViewModel() {

    private val _response =
        MutableStateFlow<NetworkResult<ImageUploadResponse>>(NetworkResult.Loading())
    val response: StateFlow<NetworkResult<ImageUploadResponse>> = _response.asStateFlow()

    private val _isImageVisible = MutableStateFlow(false)
    val isImageVisible: StateFlow<Boolean> = _isImageVisible.asStateFlow()

    private val _isPreviewVisible = MutableStateFlow(false)
    val isPreviewVisible: StateFlow<Boolean> = _isPreviewVisible.asStateFlow()

    private val _isTypeVisible = MutableStateFlow(false)
    val isTypeVisible: StateFlow<Boolean> = _isTypeVisible.asStateFlow()

    private val _isTypeName = MutableStateFlow<String?>(null)
    val isTypeName: StateFlow<String?> = _isTypeName.asStateFlow()

    private val _isUploadVisible = MutableStateFlow(false)
    val isUploadVisible: StateFlow<Boolean> = _isUploadVisible.asStateFlow()

    private val _imagePath = MutableStateFlow<String?>(null)
    val imagePath: StateFlow<String?> = _imagePath.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri.asStateFlow()

    private val _imageUrl = MutableStateFlow<String?>(null)
    val imageUrl: StateFlow<String?> = _imageUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    /**
     * Upload response
     *
     * @param profileImage
     */
    fun uploadResponse(profileImage: MultipartBody.Part) = viewModelScope.launch {
        setLoadingState(true)
        val imageFlow = repository.uploadImage(profileImage)
        imageFlow.let { flow ->
            flow.collect { values ->
                _response.value = values
                setLoadingState(false)
            }
        }
    }

    /**
     * Update image uri
     *
     * @param uri
     */
    fun updateImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    /**
     * Update image path
     *
     * @param path
     */// Function to update the image URI
    fun updateImagePath(path: String?) {
        _imagePath.value = path
        _isUploadVisible.value =
            !path.isNullOrBlank() // Show upload button only if URI is not null or blank
    }

    /**
     * Update image url
     *
     * @param url
     */// Function to update the image URL in the ViewModel
    fun updateImageUrl(url: String?) {
        _imageUrl.value = url
    }

    /**
     * Update image name
     *
     * @param imageName
     */
    fun updateImageName(imageName: String?) {
        _isTypeName.value = imageName
    }

    /**
     * Set image visibility
     *
     * @param isVisible
     */// Function to update the visibility state
    fun setImageVisibility(isVisible: Boolean) {
        _isImageVisible.value = isVisible
    }

    /**
     * Set preview visibility
     *
     * @param isVisible
     */
    fun setPreviewVisibility(isVisible: Boolean) {
        _isPreviewVisible.value = isVisible
    }

    /**
     * Set upload visibility
     *
     * @param isVisible
     */
    fun setUploadVisibility(isVisible: Boolean) {
        _isUploadVisible.value = isVisible
    }

    /**
     * Set type visibility
     *
     * @param isVisible
     */
    fun setTypeVisibility(isVisible: Boolean) {
        _isTypeVisible.value = isVisible
    }

    private fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}
