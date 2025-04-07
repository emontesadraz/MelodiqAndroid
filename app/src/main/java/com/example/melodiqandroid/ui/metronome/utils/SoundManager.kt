package com.example.melodiqandroid.ui.metronome.utils

import android.content.Context
import android.media.SoundPool
import com.example.melodiqandroid.R

class SoundManager(context: Context) {
    private val soundPool = SoundPool.Builder().setMaxStreams(1).build()
    private var tickId = 0

    /**
     * Cargar el sonido del metrónomo.
     */
    /* init {
        tickId = soundPool.load(context, R.raw.sonidoDescargadoMetronomo, 1) // Añade tu sonido en /res/raw/
    }

     */

    /**
     * El sonido del click del metrónomo.
     */
    fun playTick() {
        soundPool.play(tickId, 1f, 1f, 1, 0, 1f)
    }

    /**
     * Libera los recursos del SoundPool.
     */
    fun release() {
        soundPool.release()
    }
}