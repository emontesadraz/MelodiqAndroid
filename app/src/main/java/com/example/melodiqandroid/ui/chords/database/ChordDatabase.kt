package com.example.melodiqandroid.ui.chords.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.melodiqandroid.ui.chords.model.Chord
import com.example.melodiqandroid.ui.chords.model.ChordFileManager
import com.example.melodiqandroid.ui.chords.model.ChordType
import com.example.melodiqandroid.ui.chords.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Convertidores para los enums
class Converters {
    @TypeConverter
    fun fromNote(note: Note): String = note.name

    @TypeConverter
    fun toNote(noteName: String): Note = Note.valueOf(noteName)

    @TypeConverter
    fun fromChordType(chordType: ChordType): String = chordType.name

    @TypeConverter
    fun toChordType(chordTypeName: String): ChordType = ChordType.valueOf(chordTypeName)
}

@Database(
    entities = [Chord::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ChordDatabase : RoomDatabase() {

    abstract fun chordDao(): ChordDao

    companion object {
        @Volatile
        private var INSTANCE: ChordDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ChordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChordDatabase::class.java,
                    "chord_database"
                )
                    .addCallback(ChordDatabaseCallback(scope, context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class ChordDatabaseCallback(
        private val scope: CoroutineScope,
        private val context: Context
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.chordDao(), context)
                }
            }
        }

        private suspend fun populateDatabase(chordDao: ChordDao, context: Context) {
            // Copiar archivos desde assets a almacenamiento interno y poblar BD
            val fileManager = ChordFileManager(context)

            val initialChords = listOf(
                Chord(
                    id = 1,
                    name = "C",
                    displayName = "Do Mayor",
                    type = ChordType.MAJOR,
                    root = Note.C,
                    diagramImagePath = fileManager.copyAssetToInternalStorage("chords/images/chord_c_major.png"),
                    soundFilePath = fileManager.copyAssetToInternalStorage("res/raw/chord_c_major.mp3")
                ),
                Chord(
                    id = 2,
                    name = "D",
                    displayName = "Re Mayor",
                    type = ChordType.MAJOR,
                    root = Note.D,
                    diagramImagePath = fileManager.copyAssetToInternalStorage("chords/images/chord_d_major.png"),
                    soundFilePath = fileManager.copyAssetToInternalStorage("res/raw/chord_d_major.mp3")
                ),
                Chord(
                    id = 3,
                    name = "E",
                    displayName = "Mi Mayor",
                    type = ChordType.MAJOR,
                    root = Note.E,
                    diagramImagePath = fileManager.copyAssetToInternalStorage("chords/images/chord_e_major.png"),
                    soundFilePath = fileManager.copyAssetToInternalStorage("res/raw/chord_e_major.mp3")
                )

            )

            chordDao.insertChords(initialChords)
        }
    }
}