package org.bonhamsdemoapp.repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import org.bonhamsdemoapp.api.BaseApiResponse
import org.bonhamsdemoapp.api.ImageService
import org.bonhamsdemoapp.api.NetworkResult
import org.bonhamsdemoapp.model.ImageUploadResponse
import javax.inject.Inject

/**
 * Image repository impl
 *
 * @property imageService
 * @constructor Create Image repository impl
 */
class ImageRepositoryImpl @Inject constructor(private val imageService: ImageService) :
    ImageRepository, BaseApiResponse() {

    override suspend fun uploadImage(profileImage: MultipartBody.Part): Flow<NetworkResult<ImageUploadResponse>> {
        return flow {
            emit(safeApiCall { imageService.uploadImage(profileImage) })
        }.flowOn(Dispatchers.IO)
    }
}
