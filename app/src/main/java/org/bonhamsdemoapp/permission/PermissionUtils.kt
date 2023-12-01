package org.bonhamsdemoapp.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionUtils {

    /**
     * Check and request permission
     *
     * @param fragment
     * @param permission
     * @param onPermissionGranted
     * @param permissionLauncher
     * @receiver
     */
    fun checkAndRequestPermission(
        fragment: Fragment,
        permission: String,
        onPermissionGranted: () -> Unit,
        permissionLauncher: ActivityResultLauncher<String>
    ) {
        val isCameraPermission = permission == Manifest.permission.CAMERA
        val isStoragePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission == Manifest.permission.READ_MEDIA_IMAGES
        } else {
            permission == Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val allPermissionsGranted =
            (isCameraPermission && isPermissionGranted(fragment, Manifest.permission.CAMERA)) ||
                    (isStoragePermission && areStoragePermissionsGranted(fragment))

        if (allPermissionsGranted) {
            onPermissionGranted.invoke()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    /**
     * Is permission granted
     *
     * @param fragment
     * @param permission
     * @return
     */
    private fun isPermissionGranted(fragment: Fragment, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Are storage permissions granted
     *
     * @param fragment
     * @return
     */
    private fun areStoragePermissionsGranted(fragment: Fragment): Boolean {
        val readPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isPermissionGranted(fragment, Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            isPermissionGranted(fragment, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        return readPermission
    }
}

