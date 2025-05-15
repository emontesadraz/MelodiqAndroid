package com.example.melodiqandroid.ui.chords.model

import com.example.melodiqandroid.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ChordRepository {

    // Obtener todos los acordes disponibles
    fun getAllChords(): Flow<List<Chord>> = flow {
        emit(mockChords)
    }.flowOn(Dispatchers.IO)

    // Obtener acordes filtrados por nota raíz
    fun getChordsByRoot(root: Note): Flow<List<Chord>> = flow {
        emit(mockChords.filter { it.root == root })
    }.flowOn(Dispatchers.IO)

    // Obtener un acorde específico por ID
    fun getChordById(id: Int): Flow<Chord?> = flow {
        emit(mockChords.find { it.id == id })
    }.flowOn(Dispatchers.IO)

    // Esta es una lista de acordes simulada
    // En una aplicación real, estos datos podrían provenir de una base de datos
    private val mockChords = listOf(
        // Acordes Mayores
        Chord(1, "C", "Do Mayor", ChordType.MAJOR, Note.C, R.drawable.chord_c_major, R.raw.chord_c_major),
        //Chord(2, "D", "Re Mayor", ChordType.MAJOR, Note.D, R.drawable.chord_d_major, R.raw.chord_d_major),
        //Chord(3, "E", "Mi Mayor", ChordType.MAJOR, Note.E, R.drawable.chord_e_major, R.raw.chord_e_major),


        // Acordes Menores
        //Chord(8, "Cm", "Do Menor", ChordType.MINOR, Note.C, R.drawable.chord_c_minor, R.raw.chord_c_minor),
        //Chord(9, "Dm", "Re Menor", ChordType.MINOR, Note.D, R.drawable.chord_d_minor, R.raw.chord_d_minor),
        //Chord(10, "Em", "Mi Menor", ChordType.MINOR, Note.E, R.drawable.chord_e_minor, R.raw.chord_e_minor),
        //Chord(11, "Fm", "Fa Menor", ChordType.MINOR, Note.F, R.drawable.chord_f_minor, R.raw.chord_f_minor),
        //Chord(12, "Gm", "Sol Menor", ChordType.MINOR, Note.G, R.drawable.chord_g_minor, R.raw.chord_g_minor),
        //Chord(13, "Am", "La Menor", ChordType.MINOR, Note.A, R.drawable.chord_a_minor, R.raw.chord_a_minor),
        //Chord(14, "Bm", "Si Menor", ChordType.MINOR, Note.B, R.drawable.chord_b_minor, R.raw.chord_b_minor),

        // Acordes Séptima
        //Chord(15, "C7", "Do Séptima", ChordType.SEVENTH, Note.C, R.drawable.chord_c_seventh, R.raw.chord_c_seventh),
        //Chord(16, "D7", "Re Séptima", ChordType.SEVENTH, Note.D, R.drawable.chord_d_seventh, R.raw.chord_d_seventh),
        //Chord(17, "E7", "Mi Séptima", ChordType.SEVENTH, Note.E, R.drawable.chord_e_seventh, R.raw.chord_e_seventh),
        //Chord(18, "F7", "Fa Séptima", ChordType.SEVENTH, Note.F, R.drawable.chord_f_seventh, R.raw.chord_f_seventh),
        //Chord(19, "G7", "Sol Séptima", ChordType.SEVENTH, Note.G, R.drawable.chord_g_seventh, R.raw.chord_g_seventh),
        //Chord(20, "A7", "La Séptima", ChordType.SEVENTH, Note.A, R.drawable.chord_a_seventh, R.raw.chord_a_seventh),
        //Chord(21, "B7", "Si Séptima", ChordType.SEVENTH, Note.B, R.drawable.chord_b_seventh, R.raw.chord_b_seventh)

    )
}