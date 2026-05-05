package ru.itis.android.travel_memorize_app.map.ui.components

import android.content.Context
import android.graphics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.math.roundToInt

object MapMarkerBitmapFactory {

    suspend fun create(
        context: Context,
        photoUrl: String?,
        markerColor: Int,
        borderColor: Int,
        placeholderColor: Int
    ): Bitmap = withContext(Dispatchers.IO) {
        val density = context.resources.displayMetrics.density
        fun dp(value: Float): Float = value * density
        val width = dp(52f).roundToInt()
        val height = dp(64f).roundToInt()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val centerX = width / 2f
        val circleRadius = dp(26f)
        val circleCenterY = dp(26f)
        paint.color = Color.argb(45, 0, 0, 0)
        canvas.drawCircle(centerX, circleCenterY + dp(3f), circleRadius, paint)
        paint.color = markerColor
        canvas.drawCircle(centerX, circleCenterY, circleRadius, paint)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dp(2f)
        paint.color = borderColor
        canvas.drawCircle(centerX, circleCenterY, circleRadius - dp(1f), paint)
        paint.style = Paint.Style.FILL
        val innerRadius = dp(20f)
        val innerRect = RectF(
            centerX - innerRadius,
            circleCenterY - innerRadius,
            centerX + innerRadius,
            circleCenterY + innerRadius
        )
        val imageBitmap = photoUrl
            ?.takeIf { it.isNotBlank() }
            ?.let { loadBitmap(it) }
        if (imageBitmap == null) {
            paint.color = placeholderColor
            canvas.drawCircle(centerX, circleCenterY, innerRadius, paint)
        } else {
            val path = Path().apply {
                addCircle(centerX, circleCenterY, innerRadius, Path.Direction.CW)
            }
            canvas.save()
            canvas.clipPath(path)
            val src = calculateCenterCropSrc(imageBitmap)
            canvas.drawBitmap(imageBitmap, src, innerRect, paint)
            canvas.restore()
        }

        val pointerWidth = dp(4f)
        val pointerHeight = dp(12f)
        val pointerRect = RectF(
            centerX - pointerWidth / 2f,
            dp(48f),
            centerX + pointerWidth / 2f,
            dp(48f) + pointerHeight
        )
        paint.color = markerColor
        canvas.drawRoundRect(pointerRect, dp(999f), dp(999f), paint)
        bitmap
    }

    private fun loadBitmap(url: String): Bitmap? {
        return runCatching {
            URL(url).openStream().use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        }.getOrNull()
    }


    private fun calculateCenterCropSrc(bitmap: Bitmap): Rect {
        val width = bitmap.width
        val height = bitmap.height
        return if (width > height) {
            val left = (width - height) / 2
            Rect(left, 0, left + height, height)
        } else {
            val top = (height - width) / 2
            Rect(0, top, width, top + width)
        }


    }
}