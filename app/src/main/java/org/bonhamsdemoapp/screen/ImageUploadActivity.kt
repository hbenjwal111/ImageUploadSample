package org.bonhamsdemoapp.screen


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.bonhamsdemoapp.R
import org.bonhamsdemoapp.api.NetworkResult
import org.bonhamsdemoapp.listener.OnImageCapturedListener
import org.bonhamsdemoapp.constant.Constants.Companion.IMAGE_URL
import org.bonhamsdemoapp.databinding.ActivityMainBinding
import org.bonhamsdemoapp.extensions.PickerHandler.createProfileImage
import org.bonhamsdemoapp.model.ImageUploadResponse
import org.bonhamsdemoapp.util.NetworkMonitor
import org.bonhamsdemoapp.util.loadImageFromUrl
import org.bonhamsdemoapp.viewmodel.ImageUploadViewModel
import javax.inject.Inject


/**
 * Image upload activity
 *
 * @constructor Create  Image upload activity
 */
@AndroidEntryPoint
class ImageUploadActivity : AppCompatActivity(), OnImageCapturedListener {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: ImageUploadViewModel by viewModels()

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private var imagePath: String? = null
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        netWorkObserver()
        visibilityObserver()
        setUpClickListener()
        initObserver()
    }

    /**
     * Net work observer
     *
     */
    private fun netWorkObserver() {
        // Use the networkMonitor flow to observe network connectivity
        lifecycleScope.launch {
            networkMonitor.isConnected.collect { isConnected ->
                // Handle network connectivity changes
                if (isConnected) {
                    Log.e("networkConnected", "NetworkConnected")
                } else {
                    Log.e("networkNotConnected", "NetworkNotConnected")
                }
            }
        }
    }

    /**
     * Visibility observer
     *
     */
    private fun visibilityObserver() {
        lifecycleScope.launch {
            viewModel.isImageVisible.collectLatest { isVisible ->
                binding.imageImage.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.isPreviewVisible.collectLatest { isVisible ->
                binding.btnPreviewImageView.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.isTypeVisible.collectLatest { isVisible ->
                binding.imageFileName.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }
        lifecycleScope.launch {
            viewModel.isUploadVisible.collectLatest { isVisible ->
                binding.btnUploadImage.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
        lifecycleScope.launch {
            viewModel.imageUri.collectLatest { imageUri ->
                loadImageFromUrl(binding.imageImage, "", imageUri)
            }
        }
        lifecycleScope.launch {
            viewModel.isTypeName.collectLatest { imageName ->
                binding.imageFileName.text = imageName
            }
        }
    }

    /**
     * Init observer
     *
     */
    private fun initObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.response.collect {
                    when (it) {
                        is NetworkResult.Success -> {
                            val uploadResponse: ImageUploadResponse? = it.data
                            imageUrl = uploadResponse?.image_url
                            if (imageUrl != null) {
                                viewModel.setPreviewVisibility(true)
                                viewModel.setUploadVisibility(false)
                            } else {
                                viewModel.setPreviewVisibility(true)
                                viewModel.setUploadVisibility(false)
                            }
                        }

                        is NetworkResult.Error -> {
                            val error: String = it.message ?: "Unknown error"
                            Log.e("NetworkError", "Error occurred: $error")
                            viewModel.setPreviewVisibility(false)
                        }

                        is NetworkResult.Loading -> {
                            viewModel.setPreviewVisibility(false)
                        }
                    }
                }
            }
        }
    }

    /**
     * Set up click listener
     *
     */
    private fun setUpClickListener() {
        binding.btnCameraIntent.setOnClickListener {
            viewModel.setImageVisibility(false)
            viewModel.setTypeVisibility(false)
            val bottomSheet = ImageUploadBottomSheetDialogFragment()
            bottomSheet.show(supportFragmentManager, ImageUploadBottomSheetDialogFragment.TAG)
        }

        binding.btnUploadImage.setOnClickListener {
            val profileImage = createProfileImage(imagePath)
            viewModel.uploadResponse(profileImage)
        }

        binding.btnPreviewImageView.setOnClickListener {
            viewModel.updateImageUrl(IMAGE_URL)
            viewModel.setImageVisibility(true)
            viewModel.setTypeVisibility(true)
            viewModel.setPreviewVisibility(false)
        }
    }


    /**
     * On image captured
     *
     * @param path
     * @param imageName
     * @param imageUri
     */
    override fun onImageCaptured(path: String?, imageName: String?, imageUri: Uri?) {
        imagePath = path
        viewModel.updateImagePath(path)
        viewModel.updateImageUri(imageUri)
        viewModel.updateImageName(imageName)
    }
}
