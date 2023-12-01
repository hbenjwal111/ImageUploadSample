package org.bonhamsdemoapp.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bonhamsdemoapp.R

/**
 * Set visibility
 *
 * @param view
 * @param isVisible
 */
@BindingAdapter("viewVisibility")
fun setVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

/**
 * Load image from url
 *
 * @param imageView
 * @param imageUrl
 * @param imageUri
 */
@BindingAdapter("imageNetWorkUrl", "imageUri", requireAll = false)
fun loadImageFromUrl(imageView: AppCompatImageView, imageUrl: String?, imageUri: Uri?) {
    imageUri?.let {
        Glide.with(imageView.context)
            .load(imageUri)
            .placeholder(R.drawable.img_apple)
            .error(R.drawable.img_camera_17x17)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("ImageLoadingError", "Error loading image: $e")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(imageView)
    }
    clearGlideCache(imageView.context)
}

/**
 * Clear glide cache
 *
 * @param context
 */
@OptIn(DelicateCoroutinesApi::class)
fun clearGlideCache(context: Context) {
    // Clear memory cache
    Glide.get(context).clearMemory()

    // Clear disk cache (this should be executed in the background thread)
    GlobalScope.launch(Dispatchers.IO) {
        Glide.get(context).clearDiskCache()
    }
}
