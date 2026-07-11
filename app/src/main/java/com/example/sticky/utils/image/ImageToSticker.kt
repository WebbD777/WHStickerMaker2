package com.example.sticky.utils.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun convertImageToSticker(context: Context, sourceUri: Uri?, targetFileName: String): File? {
    if (sourceUri == null) return null
    var inputStream: InputStream? = null
    return try {
        inputStream = context.contentResolver.openInputStream(sourceUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null

        // 1. Resize the image to exactly 512x512 pixels
        val resizedBitmap = resizeBitmap(originalBitmap, 512, 512)

        // 2. Create or target the specific "test" directory in internal files
        val testDirectory = File(context.filesDir, "test")
        if (!testDirectory.exists()) {
            testDirectory.mkdirs() // Creates the "test" folder if it doesn't exist yet
        }

        // 2. Safely compress the image to target WebP format keeping file size < 100KB
        val outputFile = File(testDirectory, targetFileName) // fixed to use targetFileName
        val isCompressed = compressToWebP(resizedBitmap, outputFile)

        if (isCompressed) outputFile else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        inputStream?.close()
    }
}

/**
 * Resizes a Bitmap to exact dimensions.
 */
private fun resizeBitmap(source: Bitmap, width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(source, width, height, true)
}

/**
 * Compresses the bitmap to WebP format, dynamically reducing quality if file size exceeds 100KB.
 */
private fun compressToWebP(bitmap: Bitmap, targetFile: File): Boolean {
    val maxFileSize = 100 * 1024 // 100 KB in Bytes
    var quality = 90
    val stream = ByteArrayOutputStream()

    // Pick the correct format depending on SDK level
    val compressFormat = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Bitmap.CompressFormat.WEBP_LOSSY
        else -> @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP
    }

    // Dynamically scale down quality if it leaks over 100KB limit
    do {
        stream.reset()
        bitmap.compress(compressFormat, quality, stream)
        quality -= 10
    } while (stream.size() > maxFileSize && quality > 10)

    // Write the valid byte array stream to disk
    return try {
        FileOutputStream(targetFile).use { fos ->
            fos.write(stream.toByteArray())
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun getAllStickerUrisFromTestDirectory(context: Context): List<Uri> {
    // 1. Target the "test" directory
    val testDirectory = File(context.filesDir, "test")

    // 2. If it doesn't exist or isn't a directory, return an empty list
    if (!testDirectory.exists() || !testDirectory.isDirectory) {
        return emptyList()
    }

    // 3. List all files, filter out any directories, and map them to safe Content URIs
    return testDirectory.listFiles()
        ?.filter { it.isFile }
        ?.map { file ->
            // Converts file path to a secure content:// URI
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } ?: emptyList()
}
