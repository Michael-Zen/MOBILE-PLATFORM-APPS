package com.example.ch12.data.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.ch12.util.PROFILE_PHOTO_QUALITY
import com.example.ch12.util.PROFILE_PHOTO_SIZE
import com.example.ch12.util.calculateInSampleSize
import com.example.ch12.util.centerSquare
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

// Foto 4000x3000 -> JPEG persegi 400x400, sekitar 30-60 KB
class AndroidImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageCompressor {

    // Decode dan kompres JPEG adalah kerja CPU + I/O: jangan di main thread
    override suspend fun compress(uri: String): CompressedImage = withContext(Dispatchers.Default) {
        val source = Uri.parse(uri)

        // Tahap pertama hanya membaca dimensi; tidak mendekode seluruh bitmap.
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        open(source).use { input ->
            BitmapFactory.decodeStream(input, null, bounds)
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw ImageDecodeException("Berkas yang dipilih bukan gambar.")
        }

        // Tahap kedua mendekode versi kecil agar hemat memori.
        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(
                bounds.outWidth, bounds.outHeight, PROFILE_PHOTO_SIZE
            )
        }
        val sampled = open(source).use { input ->
            BitmapFactory.decodeStream(input, null, options)
        } ?: throw ImageDecodeException("Gambar tidak dapat didekode.")

        var cropped: Bitmap? = null
        var result: Bitmap? = null
        try {
            val crop = centerSquare(sampled.width, sampled.height)
            val cropBitmap = Bitmap.createBitmap(
                sampled, crop.left, crop.top, crop.size, crop.size
            )
            cropped = cropBitmap

            val scale = minOf(1f, PROFILE_PHOTO_SIZE.toFloat() / crop.size)
            val matrix = Matrix().apply {
                postScale(scale, scale)
                postRotate(readRotation(source).toFloat())
            }
            val outputBitmap = Bitmap.createBitmap(
                cropBitmap, 0, 0, cropBitmap.width, cropBitmap.height, matrix, true
            )
            result = outputBitmap

            val output = ByteArrayOutputStream()
            if (!outputBitmap.compress(Bitmap.CompressFormat.JPEG, PROFILE_PHOTO_QUALITY, output)) {
                throw ImageDecodeException("Foto gagal dikompres.")
            }
            CompressedImage(
                bytes = output.toByteArray(),
                originalWidth = bounds.outWidth,
                originalHeight = bounds.outHeight,
                width = outputBitmap.width,
                height = outputBitmap.height
            )
        } finally {
            if (result !== cropped) result?.recycle()
            cropped?.recycle()
            sampled.recycle()
        }
    }

    // content:// (Photo Picker) maupun file:// (CameraX) dibuka lewat ContentResolver
    @Suppress("unused")
    private fun open(uri: Uri): InputStream =
        context.contentResolver.openInputStream(uri)
            ?: throw ImageDecodeException("Gambar tidak dapat dibuka.")

    // Kamera menyimpan piksel apa adanya + tag orientasi. BitmapFactory
    // mengabaikan tag itu, sehingga tanpa ini foto potret bisa tampil miring.
    @Suppress("unused")
    private fun readRotation(uri: Uri): Int = runCatching {
        open(uri).use { ExifInterface(it).rotationDegrees }
    }.getOrDefault(0)
}
