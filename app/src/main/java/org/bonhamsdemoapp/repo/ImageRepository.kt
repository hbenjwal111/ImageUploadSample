package org.bonhamsdemoapp.repo

import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import org.bonhamsdemoapp.api.NetworkResult
import org.bonhamsdemoapp.model.ImageUploadResponse

/**
 * Image repository
 *
 * @constructor Create  Image repository
 */
interface ImageRepository {
    /**
     * Upload image
     *
     * @param profileImage
     * @return
     */
    suspend fun uploadImage(
        profileImage: MultipartBody.Part
    ): Flow<NetworkResult<ImageUploadResponse>>
}
