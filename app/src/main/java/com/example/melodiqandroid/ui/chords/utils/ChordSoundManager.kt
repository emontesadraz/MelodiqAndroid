package com.example.melodiqandroid.ui.chords.utils

import android.content.Context
import android.media.MediaPlayer
import com.example.melodiqandroid.ui.chords.model.Chord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ChordSoundPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    /**
     * Reproduce el sonido del acorde proporcionado
     */
    suspend fun playChord(chord: Chord) = withContext(Dispatchers.IO) {
        try {
            // Liberar recursos si hay un MediaPlayer activo
            releaseMediaPlayer()

            // Verificar que el archivo de sonido existe
            val soundFile = File(chord.soundFilePath)
            if (!soundFile.exists()) {
                throw Exception("Archivo de sonido no encontrado: ${chord.soundFilePath}")
            }

            // Crear y configurar un nuevo MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setDataSource(chord.soundFilePath)
                setOnCompletionListener { mp ->
                    mp.release()
                    mediaPlayer = null
                }
                prepare()
            }

            // Reproducir el sonido
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Detiene la reproducción actual si existe
     */
    fun stopPlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            releaseMediaPlayer()
        }
    }

    /**
     * Libera los recursos del MediaPlayer
     */
    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * Método para liberar recursos cuando ya no se necesita el reproductor
     */
    fun release() {
        releaseMediaPlayer()
    }
}