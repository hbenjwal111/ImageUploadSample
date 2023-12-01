package org.bonhamsdemoapp.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.bonhamsdemoapp.R
import org.bonhamsdemoapp.listener.OnImageCapturedListener
import org.bonhamsdemoapp.databinding.BottomsheetSdCardAccessBinding
import org.bonhamsdemoapp.extensions.PickerHandler.getImageFromGalleryIntent
import org.bonhamsdemoapp.extensions.PickerHandler.getImageNameFromPath
import org.bonhamsdemoapp.extensions.PickerHandler.resolveFilePathFromUri
import org.bonhamsdemoapp.extensions.PickerHandler.takePictureFromCameraIntent
import org.bonhamsdemoapp.permission.PermissionUtils
import java.io.File
import javax.inject.Inject


/**
 * Image upload bottom sheet dialog fragment
 *
 * @constructor Create empty Image upload bottom sheet dialog fragment
 */
@AndroidEntryPoint
class ImageUploadBottomSheetDialogFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var permissionUtils: PermissionUtils

    private lateinit var binding: BottomsheetSdCardAccessBinding
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null
    private var imageFile: File? = null
    private var dataPassListener: OnImageCapturedListener? = null


    /**
     * On attach
     *
     * @param context
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnImageCapturedListener) {
            dataPassListener = context
        } else {
            throw RuntimeException("$context must implement DataPassListener")
        }
    }

    /**
     * On create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        initLauncher()
    }

    /**
     * On create view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.bottomsheet_sd_card_access, container, false)
        binding.lifecycleOwner = requireActivity()
        return binding.root
    }

    /**
     * On view created
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListener()
    }

    /**
     * Init launcher
     *
     */
    private fun initLauncher() {
        cameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    // openCamera()
                } else {
                    redirectToAppSettings()
                }
            }

        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null && data.data != null) {
                        imageUri = data.data
                        imageUri?.let { uri ->
                            val filePath = resolveFilePathFromUri(uri, requireActivity())
                            val imageName = getImageNameFromPath(filePath)
                            dataPassListener?.onImageCaptured(filePath,imageName,imageUri)
                            dismiss()
                        }

                    } else {
                        imageFile?.let { file ->
                            val filePath = file.absolutePath
                            imageUri = Uri.fromFile(file)
                            val imageName = getImageNameFromPath(filePath)
                            dataPassListener?.onImageCaptured(filePath, imageName, imageUri)
                            dismiss()
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
        binding.bottomOptionOne.setOnClickListener {
            requestCameraPermission(Manifest.permission.CAMERA)
        }
        binding.bottomOptionTwo.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestCameraPermission(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                requestCameraPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    /**
     * Request camera permission
     *
     * @param grantPermission
     */
    private fun requestCameraPermission(grantPermission: String) {
        permissionUtils.checkAndRequestPermission(
            fragment = this,
            permission = grantPermission,
            onPermissionGranted = {
                if (grantPermission == Manifest.permission.CAMERA) {
                    openCameraOrGallery(false)
                } else {
                    openCameraOrGallery(true)
                }
            },
            permissionLauncher = cameraPermissionLauncher
        )
    }

    /**
     * Redirect to app settings
     *
     */
    private fun redirectToAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    /**
     * Open camera or gallery
     *
     * @param isFromGallery
     */
    private fun openCameraOrGallery(isFromGallery: Boolean) {
        val intent = if (isFromGallery) {
            val galleryIntent = getImageFromGalleryIntent()
            galleryIntent
        } else {
            val (takePictureIntent, file) = takePictureFromCameraIntent(requireActivity())
            imageFile = file
            takePictureIntent
        }
        intent.let {
            takePictureLauncher.launch(intent)
        }
    }


    companion object {
        const val TAG: String = "BaseBottomsheetDialogFragment"
    }
}
