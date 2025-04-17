package com.example.melodiqandroid.ui.metronome.utils

import android.content.Context
import android.media.SoundPool
import com.example.melodiqandroid.R

class SoundManager(context: Context) {
    private val soundPool: SoundPool
    private var tickSoundId: Int = 0

    init {
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)  // Solo necesitamos un sonido a la vez
            .build()

        // Carga el sonido
        tickSoundId = soundPool.load(context, R.raw.metronomo_click, 1)
    }

    fun playTick() {
        // Reproduce el sonido (volumen al 100% en ambos canales)
        soundPool.play(tickSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun release() {
        soundPool.release()
    }
}