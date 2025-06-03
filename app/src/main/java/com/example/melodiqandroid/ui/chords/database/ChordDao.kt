package com.example.melodiqandroid.ui.chords.database

import androidx.room.*
import com.example.melodiqandroid.ui.chords.model.Note
import com.example.melodiqandroid.ui.minigames.guessthesound.model.Chord
import kotlinx.coroutines.flow.Flow

@Dao
interface ChordDao {

    @Query("SELECT * FROM chords")
    fun getAllChords(): Flow<List<Chord>>

    @Query("SELECT * FROM chords WHERE root = :root")
    fun getChordsByRoot(root: Note): Flow<List<Chord>>

    @Query("SELECT * FROM chords WHERE id = :id")
    fun getChordById(id: Int): Flow<Chord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChord(chord: Chord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChords(chords: List<Chord>)

    @Delete
    suspend fun deleteChord(chord: Chord)

    @Query("DELETE FROM chords")
    suspend fun deleteAllChords()

    @Query("SELECT COUNT(*) FROM chords")
    suspend fun getChordsCount(): Int
}