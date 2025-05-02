package com.example.melodiqandroid.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel para la pantalla principal de la aplicación.
 * Maneja los datos que se mostrarán en el fragmento de inicio.
 */
class HomeViewModel : ViewModel() {


    /**
     * Texto de bienvenida que se mostrará en la pantalla principal.
     * Proporciona un mensaje introductorio para el usuario.
     */
    private val _text = MutableLiveData<String>().apply {
        value = "¡Bienvenido a MelodiQ!"
    }
    val text: LiveData<String> = _text

    private val _text2 = MutableLiveData<String>().apply {
        value = "Tu asistente musical para afinar instrumentos, aprender teoría musical y practicar tu oído musical."
    }

    /**
     * Texto de bienvenida que se mostrará en la pantalla principal.
     * Proporciona un mensaje introductorio para el usuario.
     */
    val text2: LiveData<String> = _text2

    private val _title = MutableLiveData<String>().apply {
        value = "MelodiQ - Tu Compañero Musical"
    }

    /**
     * Título principal de la aplicación.
     * Se utiliza para mostrar el nombre de la app en la pantalla de inicio.
     */
    val title: LiveData<String> = _title
}