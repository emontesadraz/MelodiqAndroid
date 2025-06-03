package com.example.melodiqandroid.ui.chords.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.*

class ChordFileManager(private val context: Context) {

    private val chordImagesDir = File(context.filesDir, "chord_images")
    private val chordSoundsDir = File(context.filesDir, "chord_sounds")

    init {
        // Crear directorios si no existen
        if (!chordImagesDir.exists()) {
            chordImagesDir.mkdirs()
        }
        if (!chordSoundsDir.exists()) {
            chordSoundsDir.mkdirs()
        }
    }

    /**
     * Copia un archivo desde assets al almacenamiento interno
     */
    fun copyAssetToInternalStorage(assetPath: String): String {
        val fileName = assetPath.substringAfterLast("/")
        val isImage = assetPath.contains("images")
        val targetDir = if (isImage) chordImagesDir else chordSoundsDir
        val targetFile = File(targetDir, fileName)

        if (targetFile.exists()) {
            return targetFile.absolutePath
        }

        try {
            context.assets.open(assetPath).use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }

        return targetFile.absolutePath
    }

    /**
     * Obtiene un Bitmap desde una ruta de archivo
     */
    fun getBitmapFromPath(imagePath: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(imagePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Verifica si un archivo existe
     */
    fun fileExists(filePath: String): Boolean {
        return File(filePath).exists()
    }

    /**
     * Elimina un archivo
     */
    fun deleteFile(filePath: String): Boolean {
        return try {
            File(filePath).delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Guarda un bitmap como archivo de imagen
     */
    fun saveBitmapToFile(bitmap: Bitmap, fileName: String): String {
        val file = File(chordImagesDir, fileName)
        return try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * Copia un archivo de sonido al almacenamiento interno
     */
    fun copySoundFile(sourceFile: File, fileName: String): String {
        val targetFile = File(chordSoundsDir, fileName)
        return try {
            sourceFile.copyTo(targetFile, overwrite = true)
            targetFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }
}