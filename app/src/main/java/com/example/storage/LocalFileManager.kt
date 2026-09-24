package com.example.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object LocalFileManager {

    private fun getMediaDir(context: Context): File {
        val dir = File(context.filesDir, "civil_media")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun saveUriToInternalStorage(context: Context, uri: Uri, prefix: String = "doc"): String? {
        return try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri) ?: return null
            val ext = ".jpg"
            val filename = "${prefix}_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}$ext"
            val targetFile = File(getMediaDir(context), filename)
            inputStream.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, prefix: String = "photo"): String? {
        return try {
            val filename = "${prefix}_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
            val targetFile = File(getMediaDir(context), filename)
            FileOutputStream(targetFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun fileExists(path: String?): Boolean {
        if (path.isNullOrBlank()) return false
        val f = File(path)
        return f.exists() && f.length() > 0
    }
}
