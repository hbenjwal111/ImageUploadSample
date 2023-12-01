package org.bonhamsdemoapp.listener

import android.net.Uri

/**
 * On image captured listener
 *
 * @constructor Create  On image captured listener
 */
interface OnImageCapturedListener {

    /**
     * On image captured
     *
     * @param path
     * @param imageName
     */
    fun onImageCaptured(path: String?, imageName: String?, imageUri: Uri?)
    }