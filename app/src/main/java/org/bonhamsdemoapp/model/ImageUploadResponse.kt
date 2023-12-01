package org.bonhamsdemoapp.model

import com.google.gson.annotations.SerializedName

/**
 * Image upload response
 *
 * @property success
 * @property message
 * @property image_url
 * @constructor  Image upload response
 */
data class ImageUploadResponse (
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("message")
    val message: String,
    @SerializedName("image_url")
    val image_url: String,
)
