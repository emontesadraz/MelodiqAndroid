package com.example.melodiqandroid.ui.chords.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chords")
data class Chord(
    @PrimaryKey
    val id: Int,
    val name: String,           // Nombre del acorde (Ej: "C", "Am", "G7")
    val displayName: String,    // Nombre para mostrar (Ej: "Do", "La menor", "Sol séptima")
    val type: ChordType,        // Tipo de acorde (mayor, menor, séptima, etc.)
    val root: Note,             // Nota raíz
    val diagramImagePath: String, // Ruta del archivo de imagen en storage interno
    val soundFilePath: String     // Ruta del archivo de sonido en storage interno
)

enum class Note {
    C, C_SHARP, D, D_SHARP, E, F, F_SHARP, G, G_SHARP, A, A_SHARP, B;

    fun getDisplayName(): String {
        return when(this) {
            C -> "Do"
            C_SHARP -> "Do#"
            D -> "Re"
            D_SHARP -> "Re#"
            E -> "Mi"
            F -> "Fa"
            F_SHARP -> "Fa#"
            G -> "Sol"
            G_SHARP -> "Sol#"
            A -> "La"
            A_SHARP -> "La#"
            B -> "Si"
        }
    }
}

enum class ChordType {
    MAJOR, MINOR, SEVENTH, MINOR_SEVENTH, MAJOR_SEVENTH, DIMINISHED, AUGMENTED;

    fun getDisplayName(): String {
        return when(this) {
            MAJOR -> "Mayor"
            MINOR -> "Menor"
            SEVENTH -> "Séptima"
            MINOR_SEVENTH -> "Menor Séptima"
            MAJOR_SEVENTH -> "Mayor Séptima"
            DIMINISHED -> "Disminuido"
            AUGMENTED -> "Aumentado"
        }
    }
}