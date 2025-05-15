package com.example.melodiqandroid.ui.chords.utils


import android.content.Context
import android.media.MediaPlayer
import com.example.melodiqandroid.ui.chords.model.Chord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChordSoundPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    /**
     * Reproduce el sonido del acorde proporcionado
     */
    suspend fun playChord(chord: Chord) = withContext(Dispatchers.IO) {
        try {
            // Liberar recursos si hay un MediaPlayer activo
            releaseMediaPlayer()

            // Crear y configurar un nuevo MediaPlayer
            mediaPlayer = MediaPlayer.create(context, chord.soundResourceId)
            mediaPlayer?.setOnCompletionListener { mp ->
                mp.release()
                mediaPlayer = null
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