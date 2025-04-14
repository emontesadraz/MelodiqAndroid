package com.example.melodiqandroid.ui.metronome.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.melodiqandroid.ui.metronome.model.Metronome
import com.example.melodiqandroid.ui.metronome.utils.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MetronomeViewModel(application: Application) : AndroidViewModel(application) {

    // Modelo del metrónomo (usa getApplication() para el contexto)
    private val metronome = Metronome(SoundManager(application))
    private var metronomeJob: Job? = null

    // LiveData para la UI
    private val _bpm = MutableLiveData<Int>(DEFAULT_BPM)
    val bpm: LiveData<Int> = _bpm

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _currentBeat = MutableLiveData<Int>(0)
    val currentBeat: LiveData<Int> = _currentBeat

    // Configuración del BPM
    fun setBpm(newBpm: Int) {
        val clampedBpm = newBpm.coerceIn(MIN_BPM, MAX_BPM)
        _bpm.value = clampedBpm  // ¡Esta línea es crucial!
        metronome.setBpm(clampedBpm)
    }

    // Control del metrónomo
    fun startMetronome() {
        if (_isPlaying.value == true) return

        _isPlaying.value = true
        metronomeJob = viewModelScope.launch {
            metronome.start()
        }
    }

    fun stopMetronome() {
        _isPlaying.value = false
        metronomeJob?.cancel()  // Cancela la corrutina
        metronome.stop()       // Detiene el modelo del metrónomo
    }

    companion object {
        const val MIN_BPM = 40
        const val MAX_BPM = 208
        const val DEFAULT_BPM = 60
    }
}