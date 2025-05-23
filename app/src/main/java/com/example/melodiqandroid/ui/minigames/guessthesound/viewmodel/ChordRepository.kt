package com.example.melodiqandroid.ui.minigames.guessthesound.viewmodel



import android.content.Context
import android.media.MediaPlayer
import com.example.melodiqandroid.ui.minigames.guessthesound.model.Chord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio que gestiona los datos de los acordes y la reproducción de sonidos
 */
class ChordRepository(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    /**
     * Obtiene la lista completa de acordes disponibles
     */
    suspend fun getAllChords(): List<Chord> {
        // En una implementación real, esto podría venir de una base de datos o API
        // Por ahora usamos datos de ejemplo
        return withContext(Dispatchers.IO) {
            Chord.getSampleChords()
        }
    }

    /**
     * Obtiene un subconjunto aleatorio de acordes
     */
    suspend fun getRandomChords(count: Int): List<Chord> {
        val allChords = getAllChords()
        return withContext(Dispatchers.Default) {
            allChords.shuffled().take(count.coerceAtMost(allChords.size))
        }
    }

    /**
     * Reproduce el sonido de un acorde
     */
    fun playChordSound(chord: Chord) {
        stopCurrentSound()

        // Crear y preparar el MediaPlayer con el recurso de sonido del acorde
        mediaPlayer = MediaPlayer.create(context, chord.soundResourceId).apply {
            setOnCompletionListener { mp ->
                mp.release()
                mediaPlayer = null
            }
            start()
        }
    }

    /**
     * Detiene cualquier sonido que se esté reproduciendo actualmente
     */
    fun stopCurrentSound() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
        }
    }

    /**
     * Libera recursos cuando ya no se necesita el repositorio
     */
    fun cleanup() {
        stopCurrentSound()
    }
}