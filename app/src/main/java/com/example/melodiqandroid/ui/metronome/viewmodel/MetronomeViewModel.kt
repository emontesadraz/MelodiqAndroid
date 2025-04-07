package com.example.melodiqandroid.ui.metronome.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MetronomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Esto es un metrónomo"
    }
    val text: LiveData<String> = _text
}