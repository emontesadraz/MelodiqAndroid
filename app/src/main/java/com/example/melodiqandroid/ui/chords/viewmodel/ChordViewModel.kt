package com.example.melodiqandroid.ui.chords.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.melodiqandroid.ui.chords.model.Chord
import com.example.melodiqandroid.ui.chords.model.ChordRepository
import com.example.melodiqandroid.ui.chords.model.Note
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ChordViewModel(private val repository: ChordRepository) : ViewModel() {

    // LiveData para todos los acordes
    private val _allChords = MutableLiveData<List<Chord>>()
    val allChords: LiveData<List<Chord>> = _allChords

    // LiveData para acordes filtrados
    private val _filteredChords = MutableLiveData<List<Chord>>()
    val filteredChords: LiveData<List<Chord>> = _filteredChords

    // LiveData para el acorde seleccionado actualmente
    private val _selectedChord = MutableLiveData<Chord?>()
    val selectedChord: LiveData<Chord?> = _selectedChord

    // LiveData para manejar errores
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Estado de carga
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Inicialización: cargar todos los acordes
    init {
        loadAllChords()
    }

    // Cargar todos los acordes disponibles
    fun loadAllChords() {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getAllChords()
                .catch { e ->
                    _error.value = "Error al cargar acordes: ${e.message}"
                    _isLoading.value = false
                }
                .collect { chords ->
                    _allChords.value = chords
                    _filteredChords.value = chords
                    _isLoading.value = false
                }
        }
    }

    // Filtrar acordes por nota raíz
    fun filterChordsByRoot(root: Note) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getChordsByRoot(root)
                .catch { e ->
                    _error.value = "Error al filtrar acordes: ${e.message}"
                    _isLoading.value = false
                }
                .collect { chords ->
                    _filteredChords.value = chords
                    _isLoading.value = false
                }
        }
    }

    // Seleccionar un acorde específico por ID
    fun selectChord(chordId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getChordById(chordId)
                .catch { e ->
                    _error.value = "Error al cargar el acorde: ${e.message}"
                    _isLoading.value = false
                }
                .collect { chord ->
                    _selectedChord.value = chord
                    _isLoading.value = false
                }
        }
    }

    // Seleccionar un acorde directamente
    fun selectChord(chord: Chord) {
        _selectedChord.value = chord
    }

    // Limpiar el acorde seleccionado
    fun clearSelectedChord() {
        _selectedChord.value = null
    }
}