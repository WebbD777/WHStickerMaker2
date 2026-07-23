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

/**
 * Converts a source image to a 512x512 WebP sticker and saves it in the pack's directory.
 * Returns the relative path (e.g., "1/1.webp") to be stored in the database.
 */
fun convertImageToSticker(context: Context, sourceUri: Uri?, packId: Int, stickerCount: Int): String? {
    if (sourceUri == null) return null
    var inputStream: InputStream? = null
    return try {
        inputStream = context.contentResolver.openInputStream(sourceUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null

        // 1. Resize the image to exactly 512x512 pixels
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 512, 512, true)

        // 2. Target the "packs" directory as the base, then the pack subdirectory
        val baseDir = File(context.filesDir, "packs")
        val packDir = File(baseDir, packId.toString())
        if (!packDir.exists()) {
            packDir.mkdirs()
        }

        // 3. Save with a name based on the number of stickers in the table
        val fileName = "${stickerCount + 1}.webp"
        val outputFile = File(packDir, fileName)
        
        val isCompressed = compressToWebP(resizedBitmap, outputFile)

        // Return the relative path for the DB (e.g., "1/1.webp")
        if (isCompressed) "$packId/$fileName" else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        inputStream?.close()
    }
}

/**
 * Converts a source image to a 96x96 WebP tray icon and saves it in the pack's directory.
 */
fun convertImageToTrayIcon(context: Context, sourceUri: Uri?, packId: Int): String? {
    if (sourceUri == null) return null
    var inputStream: InputStream? = null
    return try {
        inputStream = context.contentResolver.openInputStream(sourceUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null

        // Tray icon must be 96x96
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 96, 96, true)

        val baseDir = File(context.filesDir, "packs")
        val packDir = File(baseDir, packId.toString())
        if (!packDir.exists()) {
            packDir.mkdirs()
        }

        val fileName = "tray.webp"
        val outputFile = File(packDir, fileName)

        // Tray icon must be < 50KB
        val isCompressed = compressToWebP(resizedBitmap, outputFile, maxFileSize = 50 * 1024)

        if (isCompressed) "$packId/$fileName" else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        inputStream?.close()
    }
}

/**
 * Generates a safe Content URI for a sticker using the relative path stored in the DB.
 */
fun getStickerUri(context: Context, relativePath: String): Uri {
    val file = File(File(context.filesDir, "packs"), relativePath)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

/**
 * Compresses the bitmap to WebP format, dynamically reducing quality if file size exceeds limit.
 */
private fun compressToWebP(bitmap: Bitmap, targetFile: File, maxFileSize: Int = 100 * 1024): Boolean {
    var quality = 90
    val stream = ByteArrayOutputStream()

    // Pick the correct format depending on SDK level
    val compressFormat = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Bitmap.CompressFormat.WEBP_LOSSY
        else -> @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP
    }

    // Dynamically scale down quality if it leaks over limit
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
        android.util.Log.d("ImageToSticker", "Saved WebP to ${targetFile.absolutePath}, size: ${targetFile.length()} bytes")
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
