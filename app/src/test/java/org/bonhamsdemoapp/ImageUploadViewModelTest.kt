package org.bonhamsdemoapp

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.bonhamsdemoapp.api.NetworkResult
import org.bonhamsdemoapp.model.ImageUploadResponse
import org.bonhamsdemoapp.repo.ImageRepository
import org.bonhamsdemoapp.viewmodel.ImageUploadViewModel
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

/**
 * Image upload view model test
 *
 * @constructor Create Image upload view model test
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ImageUploadViewModelTest {
    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: ImageRepository

    private lateinit var viewModel: ImageUploadViewModel

    @ExperimentalCoroutinesApi
    private val testDispatcher = StandardTestDispatcher()

    /**
     * Set up
     *
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ImageUploadViewModel(repository)
    }

    /**
     * Tear down
     *
     */
    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    /**
     * Upload response_success
     *
     */
    @Test
    fun uploadResponse_success() = runTest(StandardTestDispatcher()) {
        val mockImageUploadResponse =
            ImageUploadResponse(true, "Success message", "mockImageUrl")

        val mockNetworkResult: NetworkResult<ImageUploadResponse> =
            NetworkResult.Success(mockImageUploadResponse)
        val imageFile = File("IMAGE_URL")
        val requestFile = imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val partFiles = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
        viewModel = ImageUploadViewModel(repository)


        val job = launch {
            `when`(repository.uploadImage(partFiles)).thenReturn(flow { emit(mockNetworkResult) })
            viewModel.uploadResponse(partFiles)
            viewModel.response.collect { result ->
                if (result is NetworkResult.Success) {
                    result.data?.let { data ->
                        Assert.assertTrue(data.success)
                        assertEquals(data.message, "Success message")
                        assertEquals(data.image_url, "mockImageUrl")
                    }
                }
            }
        }
        job.cancel()
    }


    /**
     * Update image path
     *
     */
    @Test
    fun updateImagePath() {
        val expectedPath = "content://media/external/images/media/123"
        viewModel.updateImagePath(expectedPath)
        assertEquals(expectedPath, viewModel.imagePath.value)
    }

    /**
     * Update image uri
     *
     */
    @Test
    fun updateImageUri() {
        val imageUri: Uri = mock(Uri::class.java)
        viewModel.updateImageUri(imageUri)
        assertEquals(imageUri, viewModel.imageUri.value)
    }

    /**
     * Update image url
     *
     */
    @Test
    fun updateImageUrl() {
        val expectedUrl = "http://image-url.com"
        viewModel.updateImageUrl(expectedUrl)
        assertEquals(expectedUrl, viewModel.imageUrl.value)
    }

    /**
     * Set image visibility
     *
     */
    @Test
    fun setImageVisibility() {
        val expectedVisibility = true
        viewModel.setImageVisibility(expectedVisibility)
        assertEquals(expectedVisibility, viewModel.isImageVisible.value)
    }

    /**
     * Set preview visibility
     *
     */
    @Test
    fun setPreviewVisibility() {
        val expectedVisibility = true
        viewModel.setPreviewVisibility(expectedVisibility)
        assertEquals(expectedVisibility, viewModel.isPreviewVisible.value)
    }

    /**
     * Set type visibility
     *
     */
    @Test
    fun setTypeVisibility() {
        val expectedVisibility = true
        viewModel.setTypeVisibility(expectedVisibility)
        assertEquals(expectedVisibility, viewModel.isTypeVisible.value)
    }

    /**
     * Set upload visibility
     *
     */
    @Test
    fun setUploadVisibility() {
        val expectedVisibility = true
        viewModel.setUploadVisibility(expectedVisibility)
        assertEquals(expectedVisibility, viewModel.isUploadVisible.value)
    }
}
