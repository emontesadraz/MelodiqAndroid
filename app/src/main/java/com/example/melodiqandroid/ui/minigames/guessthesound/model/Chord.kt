package com.example.melodiqandroid.ui.minigames.guessthesound.model

import com.example.melodiqandroid.R

/**
 * Representa un acorde con su nombre, código de sonido y otros atributos
 */
data class Chord(
    val id: String,            // Identificador único del acorde
    val displayName: String,   // Nombre a mostrar (ej: "Do Mayor")
    val notation: String,      // Notación musical (ej: "C")
    val soundResourceId: Int,  // ID del recurso de audio

    // Para extender en el futuro, por ejemplo con descripciones adicionales o una imagen
    val description: String? = null,
    val imageResourceId: Int? = null
) {
    /**
     * Método de utilidad para saber si dos acordes son musicalmente iguales
     */

    fun isSameChord(other: Chord): Boolean {
        return this.id == other.id
    }

    companion object {

        fun getSampleChords(): List<Chord> {
            return listOf(
                Chord("c_major", "Do Mayor", "C", R.raw.chord_c_major),
                Chord("g_major", "Sol Mayor", "G", R.raw.chord_g_major),
                Chord("a_minor", "La menor", "Am", R.raw.chord_a_major),
                Chord("f_major", "Fa Mayor", "F", R.raw.chord_f_major),
                Chord("d_minor", "Re menor", "Dm", R.raw.chord_d_major),
                Chord("e_major", "Mi Mayor", "E", R.raw.chord_e_major),
                Chord("b_minor", "Si menor", "Bm", R.raw.chord_b_major)
            )
        }
    }
}