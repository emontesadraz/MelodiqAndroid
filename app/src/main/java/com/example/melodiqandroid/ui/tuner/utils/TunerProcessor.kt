package com.example.melodiqandroid.ui.tuner.utils

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlin.math.log10
import kotlin.math.pow

@SuppressLint("MissingPermission")
class TunerProcessor {
    private val audioRecorder: AudioRecord
    private val bufferSize: Int
    private val audioData: ShortArray
    private val sampleRate = 44100

    init {
        bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioData = ShortArray(bufferSize)

        audioRecorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
    }

    fun start() {
        if (audioRecorder.state != AudioRecord.STATE_INITIALIZED) return
        audioRecorder.startRecording()
    }

    fun stop() {
        if (audioRecorder.state != AudioRecord.STATE_INITIALIZED) return
        audioRecorder.stop()
    }

    fun release() {
        audioRecorder.release()
    }

    fun detectPitch(): Float {
        if (audioRecorder.state != AudioRecord.STATE_INITIALIZED) return -1f

        audioRecorder.read(audioData, 0, bufferSize)

        // Implementación básica usando algoritmo de autocorrelación
        val frequency = findFundamentalFrequency()
        return frequency
    }

    private fun findFundamentalFrequency(): Float {
        // Convertimos los datos de audio a un array de doubles para el procesamiento
        val doubleArray = DoubleArray(bufferSize)
        for (i in 0 until bufferSize) {
            doubleArray[i] = audioData[i].toDouble()
        }

        // Implementación simple del algoritmo YIN para detección de pitch
        // (versión simplificada para este ejemplo)
        val threshold = 0.2
        val minFreq = 50.0 // Hz
        val maxFreq = 1000.0 // Hz

        val minPeriod = (sampleRate / maxFreq).toInt()
        val maxPeriod = (sampleRate / minFreq).toInt()

        val yinBuffer = DoubleArray(bufferSize / 2)

        // Paso 1: Función de diferencia autocorrelacionada
        for (tau in 0 until yinBuffer.size) {
            yinBuffer[tau] = 0.0
            for (i in 0 until yinBuffer.size) {
                val delta = doubleArray[i] - doubleArray[i + tau]
                yinBuffer[tau] += delta * delta
            }
        }

        // Paso 2: Normalización de la función de diferencia
        var runningSum = 0.0
        yinBuffer[0] = 1.0
        for (tau in 1 until yinBuffer.size) {
            runningSum += yinBuffer[tau]
            yinBuffer[tau] *= tau / runningSum
        }

        // Paso 3: Encontrar el período
        var tau = minPeriod
        while (tau < maxPeriod) {
            if (yinBuffer[tau] < threshold) {
                // Encontramos un mínimo local
                var betterTau = tau
                while (tau + 1 < maxPeriod && yinBuffer[tau + 1] < yinBuffer[tau]) {
                    betterTau = tau + 1
                    tau++
                }
                // Aproximación parabólica para mejorar la precisión
                val y1 = yinBuffer[betterTau - 1]
                val y2 = yinBuffer[betterTau]
                val y3 = yinBuffer[betterTau + 1]
                val refinedTau = betterTau + 0.5 * (y1 - y3) / (y1 - 2 * y2 + y3)

                return sampleRate.toFloat() / refinedTau.toFloat()
            }
            tau++
        }

        return -1f // No se encontró la frecuencia
    }

    // Método para convertir una frecuencia a una nota musical
    fun frequencyToNote(frequency: Float): NoteData {
        if (frequency <= 0) return NoteData("--", 0f)

        // A4 = 440Hz
        val a4 = 440.0
        val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

        // Calculamos cuántos semitonos está la frecuencia desde A4
        val semitonesFromA4 = 12 * log10(frequency / a4) / log10(2.0)

        // Calculamos el índice de la nota
        val octave = (semitonesFromA4 / 12 + 4).toInt()
        var noteIndex = ((semitonesFromA4 % 12) + 12) % 12

        // Calculamos el índice de la nota más cercana
        val noteIndexInt = Math.round(noteIndex).toInt()
        noteIndex = noteIndexInt.toDouble()

        // Calculamos la frecuencia exacta de la nota más cercana
        val exactFrequency = a4 * 2.0.pow((noteIndexInt - 9 + (octave - 4) * 12) / 12.0)

        // Calculamos la desviación en cents
        val cents = 1200 * log10(frequency / exactFrequency) / log10(2.0)

        val noteName = noteNames[((noteIndexInt % 12) + 12) % 12] + octave

        return NoteData(noteName, cents.toFloat())
    }

    data class NoteData(val note: String, val cents: Float)
}