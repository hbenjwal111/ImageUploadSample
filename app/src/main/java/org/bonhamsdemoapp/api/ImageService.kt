package org.bonhamsdemoapp.api

import okhttp3.MultipartBody
import org.bonhamsdemoapp.constant.Constants
import org.bonhamsdemoapp.model.ImageUploadResponse
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageService {
    /**
     * Upload image
     *
     * @param profileImage
     * @return
     */
    @Multipart
    @POST(Constants.RANDOM_URL)
    suspend fun uploadImage(
        @Part profileImage: MultipartBody.Part
    ): Response<ImageUploadResponse>
}
