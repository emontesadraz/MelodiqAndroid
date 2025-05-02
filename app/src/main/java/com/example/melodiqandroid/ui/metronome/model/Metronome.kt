package com.example.melodiqandroid.ui.metronome.model

import com.example.melodiqandroid.ui.utils.SoundManager
import kotlinx.coroutines.delay

class Metronome(private val soundManager: SoundManager) {
    private var isPlaying = false
    private var bpm = 60

    suspend fun start() {
        isPlaying = true
        while (isPlaying) {
            soundManager.playTick()  // ¡Aquí se reproduce el sonido!
            delay(60000L / bpm)      // Pausa calculada según el BPM
        }
    }

    fun stop() {
        isPlaying = false
    }

    fun setBpm(newBpm: Int) {
        bpm = newBpm.coerceIn(40, 208)
    }
}