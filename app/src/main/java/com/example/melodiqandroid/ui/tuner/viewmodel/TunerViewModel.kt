package com.example.melodiqandroid.ui.tuner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.melodiqandroid.ui.tuner.model.TunerProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TunerViewModel : ViewModel() {
    private val tunerProcessor = TunerProcessor()

    private val _noteData = MutableLiveData<TunerProcessor.NoteData>()
    val noteData: LiveData<TunerProcessor.NoteData> = _noteData

    private val _isListening = MutableLiveData<Boolean>()
    val isListening: LiveData<Boolean> = _isListening

    private var tunerJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun PeqstartListening() {
        if (_isListening.value == true) return

        tunerProcessor.start()
        _isListening.value = true

        tunerJob = coroutineScope.launch {
            while (isActive) {
                withContext(Dispatchers.IO) {
                    val frequency = tunerProcessor.detectPitch()
                    if (frequency > 0) {
                        val note = tunerProcessor.frequencyToNote(frequency)
                        withContext(Dispatchers.Main) {
                            _noteData.value = note
                        }
                    }
                }
                delay(100) // Actualizar 10 veces por segundo
            }
        }
    }

    fun stopListening() {
        tunerJob?.cancel()
        tunerProcessor.stop()
        _isListening.value = false
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
        tunerProcessor.release()
        coroutineScope.cancel()
    }
}