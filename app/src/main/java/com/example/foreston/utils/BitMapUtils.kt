package com.example.foreston.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object BitMapUtils {
    private const val COMPRESSION_QUALITY = 100

    fun bitmapFromString(string: String): Bitmap {
        val decodedString: ByteArray = Base64.decode(string, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun bitmapToString(bitmap: Bitmap): String {
        val byteArrayBitmapStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.PNG,
            COMPRESSION_QUALITY,
            byteArrayBitmapStream
        )
        val byteArray = byteArrayBitmapStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}