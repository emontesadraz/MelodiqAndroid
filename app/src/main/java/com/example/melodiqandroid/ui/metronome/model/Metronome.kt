package com.example.melodiqandroid.ui.metronome.model

import com.example.melodiqandroid.ui.metronome.utils.SoundManager
import kotlinx.coroutines.delay

class Metronome(private val soundManager: SoundManager) {
    private var isPlaying = false
    private var bpm = 60
    private val delay: Long get() = 60000L / bpm // ms por pulso

    fun setBpm(newBpm: Int) {
        bpm = newBpm.coerceIn(40, 208) // Rango típico
    }

    suspend fun start() {
        isPlaying = true
        while (isPlaying) {
            soundManager.playTick()
            delay(delay)
        }
    }

    fun stop() {
        isPlaying = false
    }
}