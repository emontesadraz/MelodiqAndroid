package com.example.melodiqandroid.ui.minigames.guessthesound.model

/**
 * Representa el resultado de una partida del juego de adivinar acordes
 */
data class Score(
    val totalRounds: Int,          // Total de rondas jugadas
    val correctAnswers: Int,       // Respuestas correctas
    val score: Int,                // Puntuación total
    val details: List<RoundDetail> // Detalles por ronda
) {
    /**
     * Calcula el porcentaje de acierto
     */
    fun getSuccessPercentage(): Int {
        if (totalRounds == 0) return 0
        return (correctAnswers * 100) / totalRounds
    }

    /**
     * Devuelve un mensaje de feedback basado en el rendimiento
     */
    fun getFeedbackMessage(): String {
        return when (getSuccessPercentage()) {
            in 0..40 -> "¡Sigue practicando! El entrenamiento mejorará tu oído musical."
            in 41..70 -> "¡Buen trabajo! Estás mejorando tu capacidad para reconocer acordes."
            in 71..99 -> "¡Excelente! Tienes un buen oído musical."
            100 -> "¡Perfecto! Tienes un oído musical excepcional."
            else -> "¡Gracias por jugar!"
        }
    }
}

/**
 * Detalle de cada ronda del juego
 */
data class RoundDetail(
    val roundNumber: Int,         // Número de ronda
    val playedChord: Chord,       // Acorde que se reprodujo
    val selectedChord: Chord?,    // Acorde que seleccionó el usuario (null si no seleccionó)
    val isCorrect: Boolean,       // Si la respuesta fue correcta
    val timeSpentMs: Long         // Tiempo que tardó en responder (milisegundos)
)