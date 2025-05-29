package com.example.melodiqandroid.ui.minigames.guessthesound.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.melodiqandroid.ui.minigames.guessthesound.model.Chord
import com.example.melodiqandroid.ui.minigames.guessthesound.model.RoundDetail
import com.example.melodiqandroid.ui.minigames.guessthesound.model.Score
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random

/**
 * ViewModel para el juego de adivinar acordes
 */
class ChordGameViewModel(application: Application) : AndroidViewModel(application) {

    // Repositorio para obtener datos y gestionar sonidos
    private val chordRepository = ChordRepository(application)

    // Número total de rondas en una partida
    private val totalRounds = 5

    // Número de opciones de acordes a mostrar
    private val numberOfOptions = 4

    // Tiempo máximo para responder en milisegundos (30 segundos)
    private val maxTimePerRound = 30000L

    // Acordes disponibles para el juego actual
    private var gameChords: List<Chord> = emptyList()

    // Acorde actual que debe ser adivinado
    private var currentChord: Chord? = null

    // Ronda actual
    private var currentRound = 0

    // Puntuación actual
    private var score = 0

    // Tiempo de inicio de la ronda actual
    private var roundStartTime = 0L

    // Detalles de todas las rondas jugadas
    private val roundDetails = mutableListOf<RoundDetail>()

    // ESTADOS DEL JUEGO

    // Estado actual del juego
    private val _gameState = MutableLiveData<GameState>(GameState.NotStarted)
    val gameState: LiveData<GameState> = _gameState

    // Lista de acordes para mostrar como opciones
    private val _chordOptions = MutableLiveData<List<Chord>>()
    val chordOptions: LiveData<List<Chord>> = _chordOptions

    // Información de la ronda actual
    private val _roundInfo = MutableLiveData<RoundInfo>()
    val roundInfo: LiveData<RoundInfo> = _roundInfo

    // Resultado de la ronda actual
    private val _roundResult = MutableLiveData<RoundResult>()
    val roundResult: LiveData<RoundResult> = _roundResult

    // Resultado final del juego
    private val _gameResult = MutableLiveData<Score>()
    val gameResult: LiveData<Score> = _gameResult

    // Inicializa el juego cargando los acordes disponibles
    init {
        loadChords()
    }

    private fun loadChords() {
        viewModelScope.launch {
            try {
                _gameState.value = GameState.Loading
                gameChords = chordRepository.getAllChords()
                _gameState.value = GameState.Ready
            } catch (e: Exception) {
                _gameState.value = GameState.Error("Error al cargar los acordes: ${e.message}")
            }
        }
    }

    /**
     * Inicia una nueva partida
     */
    fun startGame() {
        if (gameChords.isEmpty()) {
            _gameState.value = GameState.Error("No hay acordes disponibles")
            return
        }

        viewModelScope.launch {
            // Reiniciar el estado del juego
            currentRound = 0
            score = 0
            roundDetails.clear()

            // Iniciar la primera ronda
            startNextRound()
        }
    }

    /**
     * Inicia la siguiente ronda del juego
     */
    private fun startNextRound() {
        if (currentRound >= totalRounds) {
            // El juego ha terminado, mostrar resultados
            finishGame()
            return
        }

        viewModelScope.launch {
            currentRound++

            // Seleccionar aleatoriamente el acorde a adivinar
            val randomChords = gameChords.shuffled().take(numberOfOptions)
            val targetChord = randomChords.random()

            currentChord = targetChord
            roundStartTime = System.currentTimeMillis()

            // Actualizar estado del juego
            _chordOptions.value = randomChords
            _roundInfo.value = RoundInfo(currentRound, totalRounds, score)
            _gameState.value = GameState.Playing
        }
    }

    /**
     * Reproduce el sonido del acorde actual
     */
    fun playCurrentChord() {
        currentChord?.let {
            chordRepository.playChordSound(it)
        }
    }

    /**
     * Reproduce el sonido de un acorde de muestra
     */
    fun playSampleChord(chord: Chord) {
        chordRepository.playChordSound(chord)
    }

    /**
     * Procesa la selección de un acorde por parte del usuario
     */
    fun selectChord(selectedChord: Chord) {
        if (_gameState.value != GameState.Playing) return

        val current = currentChord ?: return
        val timeSpent = System.currentTimeMillis() - roundStartTime
        val isCorrect = current.isSameChord(selectedChord)

        // Calcular puntos (más puntos si responde rápido)
        val points = if (isCorrect) {
            val timeBonus = ((maxTimePerRound - timeSpent).coerceAtLeast(0) / 1000).toInt()
            10 + timeBonus
        } else {
            0
        }

        // Actualizar puntuación
        if (isCorrect) {
            score += points
        }

        // Guardar detalles de la ronda
        val roundDetail = RoundDetail(
            roundNumber = currentRound,
            playedChord = current,
            selectedChord = selectedChord,
            isCorrect = isCorrect,
            timeSpentMs = timeSpent
        )
        roundDetails.add(roundDetail)

        // Notificar resultado de la ronda
        _roundResult.value = RoundResult(
            isCorrect = isCorrect,
            correctChord = current,
            selectedChord = selectedChord,
            points = points
        )

        // Pausa breve para mostrar feedback antes de la siguiente ronda
        viewModelScope.launch {
            _gameState.value = GameState.RoundFeedback
            delay(1500) // 1.5 segundos de feedback
            startNextRound()
        }
    }

    /**
     * Finaliza el juego y calcula el resultado final
     */
    private fun finishGame() {
        val correctAnswers = roundDetails.count { it.isCorrect }

        val gameResult = Score(
            totalRounds = totalRounds,
            correctAnswers = correctAnswers,
            score = score,
            details = roundDetails.toList()
        )

        _gameResult.value = gameResult
        _gameState.value = GameState.Finished
    }

    /**
     * Detiene el juego actual
     */
    fun stopGame() {
        chordRepository.stopCurrentSound()
        _gameState.value = GameState.Ready
    }

    override fun onCleared() {
        super.onCleared()
        chordRepository.cleanup()
    }
}

/**
 * Estados posibles del juego
 */
sealed class GameState {
    object NotStarted : GameState()
    object Loading : GameState()
    object Ready : GameState()
    object Playing : GameState()
    object RoundFeedback : GameState()
    object Finished : GameState()
    data class Error(val message: String) : GameState()
}

/**
 * Información de la ronda actual
 */
data class RoundInfo(
    val currentRound: Int,
    val totalRounds: Int,
    val currentScore: Int
)

/**
 * Resultado de una ronda
 */
data class RoundResult(
    val isCorrect: Boolean,
    val correctChord: Chord,
    val selectedChord: Chord,
    val points: Int
)