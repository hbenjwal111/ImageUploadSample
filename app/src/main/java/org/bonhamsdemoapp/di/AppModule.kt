package org.bonhamsdemoapp.di

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import org.bonhamsdemoapp.api.ImageService
import org.bonhamsdemoapp.constant.Constants.Companion.BASE_URL
import org.bonhamsdemoapp.permission.PermissionUtils
import org.bonhamsdemoapp.repo.ImageRepository
import org.bonhamsdemoapp.repo.ImageRepositoryImpl
import org.bonhamsdemoapp.util.NetworkMonitor
import org.bonhamsdemoapp.viewmodel.ImageUploadViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /**
     * Provide image upload view model
     *
     * @param repository
     * @return
     */
    @Provides
    @Singleton
    fun provideImageUploadViewModel(repository: ImageRepository): ImageUploadViewModel {
        return ImageUploadViewModel(repository)
    }

    /**
     * Provide image repository
     *
     * @param imageService
     * @return
     */
    @Provides
    @Singleton
    fun provideImageRepository(imageService: ImageService): ImageRepository {
        return ImageRepositoryImpl(imageService)
    }


    /**
     * Provide http client
     *
     * @return
     */
    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Provide converter factory
     *
     * @return
     */
    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    /**
     * Provide retrofit
     *
     * @param okHttpClient
     * @param gsonConverterFactory
     * @return
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    /**
     * Provide image upload service
     *
     * @param retrofit
     * @return
     */
    @Provides
    @Singleton
    fun provideImageUploadService(retrofit: Retrofit): ImageService =
        retrofit.create(ImageService::class.java)

    /**
     * Provide permission utils
     *
     * @return
     */
    @Provides
    @Singleton
    fun providePermissionUtils(): PermissionUtils {
        return PermissionUtils
    }

    /**
     * Provide connectivity manager
     *
     * @param context
     * @return
     */
    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Provide network monitor
     *
     * @param connectivityManager
     * @return
     */
    @Provides
    fun provideNetworkMonitor(connectivityManager: ConnectivityManager): NetworkMonitor {
        return NetworkMonitor(connectivityManager)
    }
}

