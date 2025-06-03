package com.example.melodiqandroid.ui.chords.model

import com.example.melodiqandroid.ui.chords.database.ChordDao
import kotlinx.coroutines.flow.Flow

class ChordRepository(private val chordDao: ChordDao) {

    // Obtener todos los acordes disponibles
    fun getAllChords(): Flow<List<Chord>> = chordDao.getAllChords()

    // Obtener acordes filtrados por nota raíz
    fun getChordsByRoot(root: Note): Flow<List<Chord>> = chordDao.getChordsByRoot(root)

    // Obtener un acorde específico por ID
    fun getChordById(id: Int): Flow<Chord?> = chordDao.getChordById(id)

    // Insertar un nuevo acorde
    suspend fun insertChord(chord: Chord) = chordDao.insertChord(chord)

    // Insertar múltiples acordes
    suspend fun insertChords(chords: List<Chord>) = chordDao.insertChords(chords)

    // Eliminar un acorde
    suspend fun deleteChord(chord: Chord) = chordDao.deleteChord(chord)

    // Eliminar todos los acordes
    suspend fun deleteAllChords() = chordDao.deleteAllChords()

    // Obtener el número total de acordes
    suspend fun getChordsCount(): Int = chordDao.getChordsCount()
}