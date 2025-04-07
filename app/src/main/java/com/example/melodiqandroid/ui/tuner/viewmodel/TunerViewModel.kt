package com.example.melodiqandroid.ui.tuner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TunerViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Esto es un afinador"
    }
    val text: LiveData<String> = _text
}