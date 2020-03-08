package com.app.pixman.utils

import android.content.Context
import android.graphics.*
import android.util.TypedValue
import android.view.View
import com.app.pixman.R

object Utility {
    fun getDPFromPixel(pixel: Float, context: Context): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        pixel,
        context.resources.displayMetrics
    )

    fun getPixelFromDP(dp: Float, context: Context): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        dp,
        context.resources.displayMetrics
    )


    private fun flipImageHorizontally(src: Bitmap): Bitmap {
        val matrix = Matrix().also {
            it.preScale(-1f, 1f)
        }
        return Bitmap.createBitmap(
            src, 0, 0, src.width, src.height, matrix, true
        )
    }

    fun flipImage(src: Bitmap, flipType: FlipType): Bitmap {
        val matrix = Matrix().also {
            when (flipType) {
                FlipType.Horizontally -> it.preScale(-1f, 1f)
                FlipType.Vertically -> it.preScale(1f, -1f)
            }
        }
        return Bitmap.createBitmap(
            src, 0, 0, src.width, src.height, matrix, true
        )
    }

    fun setImageOpacity(src: Bitmap, alpha: Int): Bitmap? {
        return try {
            var outputBitmap: Bitmap = src.copy(Bitmap.Config.ARGB_8888, true)
            var finalBitmap = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(finalBitmap)
            val paint = Paint(Paint.FILTER_BITMAP_FLAG)
            paint.alpha = alpha
            canvas.drawBitmap(outputBitmap, 0f, 0f, paint)
            finalBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun addTextToImage(src: Bitmap, layout: View): Bitmap? {
        return try {
            var outputBitmap: Bitmap = src.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(outputBitmap)
            layout.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );

            layout.layout(0, 0, layout.measuredWidth, layout.measuredHeight);
            layout.isDrawingCacheEnabled = true
            layout.buildDrawingCache()
            val imageBitmap = layout.drawingCache
            canvas.drawBitmap(
                imageBitmap,
                src.width - layout.width - getDPFromPixel(8f, layout.context),
                src.height - layout.height - getDPFromPixel(8f, layout.context),
                Paint()
            )
            outputBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun addBitmap(src: Bitmap, context: Context): Bitmap? {
        return try {
            var sourceBitmap: Bitmap =
                if (src.isMutable) src else src.copy(
                    Bitmap.Config.ARGB_8888,
                    true
                )
            val canvas = Canvas(sourceBitmap)
            val logo = BitmapFactory.decodeResource(context.resources, R.drawable.logo)
            canvas.drawBitmap(
                logo,
                0f + getDPFromPixel(8f, context),
                (src.height - logo.height - getDPFromPixel(8f, context)),
                Paint()
            )
            sourceBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

object RequestCode {
    const val ImagePicker = 0x0000
}

object ArgumentKey {
    const val ImageUri = "image_uri"
}

sealed class FlipType {
    object Vertically : FlipType()
    object Horizontally : FlipType()
}