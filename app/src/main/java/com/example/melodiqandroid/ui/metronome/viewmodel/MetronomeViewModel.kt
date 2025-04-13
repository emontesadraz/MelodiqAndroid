package com.example.melodiqandroid.ui.metronome.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melodiqandroid.ui.metronome.model.Metronome
import com.example.melodiqandroid.ui.metronome.utils.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MetronomeViewModel(private val context: Context) : ViewModel() {

    // Constantes
    companion object {
        const val MIN_BPM = 40
        const val MAX_BPM = 208
        const val DEFAULT_BPM = 60
    }

    // Modelo del metrónomo
    private val metronome = Metronome(SoundManager(context))
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
        _bpm.value = clampedBpm
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
        metronomeJob?.cancel()
        metronome.stop()
    }

    fun toggleMetronome() {
        if (_isPlaying.value == true) {
            stopMetronome()
        } else {
            startMetronome()
        }
    }

}