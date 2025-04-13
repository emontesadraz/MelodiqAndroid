package com.example.melodiqandroid.ui.metronome.model

import com.example.melodiqandroid.ui.metronome.utils.SoundManager
import kotlinx.coroutines.delay

class Metronome(private val soundManager: SoundManager) {
    private var isPlaying = false
    private var bpm = 60
    private val delay: Long get() = 60000L / bpm // ms por pulso

    /**
     * Devuelve el BPM actual del metrónomo.
     */
    /**
     * Cambia el BPM del metrónomo.
     * @param newBpm El nuevo BPM. Debe estar entre 40 y 208.
     */
    fun setBpm(newBpm: Int) {
        bpm = newBpm.coerceIn(40, 208) // Rango típico
    }

    /**
     * Inicia el metrónomo.
     * Llama a [SoundManager.playTick] cada vez que pasa un pulso.
     */
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