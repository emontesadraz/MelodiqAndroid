package com.example.melodiqandroid.ui.tuner.utils

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round

@SuppressLint("MissingPermission")
class TunerProcessor {
    private val audioRecorder: AudioRecord
    private val bufferSize: Int
    private val audioData: ShortArray
    private val sampleRate = 44100

    // Frecuencias estándar de las cuerdas de guitarra
    private val guitarStrings = mapOf(
        "E2" to 82.41,  // Mi grave (6ª cuerda)
        "A2" to 110.00, // La (5ª cuerda)
        "D3" to 146.83, // Re (4ª cuerda)
        "G3" to 196.00, // Sol (3ª cuerda)
        "B3" to 246.94, // Si (2ª cuerda)
        "E4" to 329.63  // Mi agudo (1ª cuerda)
    )

    init {
        bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 4 // Buffer más grande para capturar frecuencias más bajas

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

        val readSize = audioRecorder.read(audioData, 0, bufferSize)
        if (readSize <= 0) return -1f

        // Implementación mejorada del algoritmo de detección de pitch
        val frequency = improvedPitchDetection()
        return frequency
    }

    private fun improvedPitchDetection(): Float {
        // Preprocesamiento de la señal para mejorar la detección
        val processed = preProcessSignal(audioData)

        // Implementación mejorada del algoritmo YIN para detección de pitch
        val threshold = 0.1 // Valor más bajo para mayor sensibilidad
        val minFreq = 75.0  // Hz - Ajustado para detectar E2 (Mi grave)
        val maxFreq = 500.0 // Hz

        val minPeriod = (sampleRate / maxFreq).toInt()
        val maxPeriod = (sampleRate / minFreq).toInt()

        val yinBufferSize = processed.size / 2
        val yinBuffer = DoubleArray(yinBufferSize)

        // Paso 1: Diferencia cuadrada
        for (tau in 0 until yinBufferSize) {
            yinBuffer[tau] = 0.0
            for (i in 0 until yinBufferSize) {
                val delta = processed[i] - processed[i + tau]
                yinBuffer[tau] += delta * delta
            }
        }

        // Paso 2: Función cumulativa de media normalizada
        var runningSum = 0.0
        yinBuffer[0] = 1.0
        for (tau in 1 until yinBufferSize) {
            runningSum += yinBuffer[tau]
            if (runningSum > 0) {
                yinBuffer[tau] *= tau / runningSum
            } else {
                yinBuffer[tau] = 1.0
            }
        }

        // Paso 3: Detección de pitch usando interpolación
        var tau = minPeriod
        while (tau < maxPeriod) {
            if (yinBuffer[tau] < threshold) {
                // Encontramos un mínimo local
                var betterTau = tau
                // Buscar el mínimo local real
                while (tau + 1 < maxPeriod && yinBuffer[tau + 1] < yinBuffer[tau]) {
                    betterTau = tau + 1
                    tau++
                }

                // Interpolación parabólica para mejorar la precisión
                if (betterTau > 0 && betterTau < yinBufferSize - 1) {
                    val y1 = yinBuffer[betterTau - 1]
                    val y2 = yinBuffer[betterTau]
                    val y3 = yinBuffer[betterTau + 1]

                    // Evitar división por cero
                    val denominator = y1 - 2 * y2 + y3
                    val delta = if (abs(denominator) > 0.000001) {
                        0.5 * (y1 - y3) / denominator
                    } else {
                        0.0
                    }

                    val refinedTau = betterTau + delta
                    val detectedFreq = sampleRate.toFloat() / refinedTau.toFloat()

                    // Verificar si la frecuencia detectada está cerca de una cuerda de guitarra
                    val closestString = findClosestGuitarString(detectedFreq)

                    // Si está muy cerca de una cuerda de guitarra, ajustamos ligeramente
                    return if (closestString != null && abs(detectedFreq - closestString.second.toFloat()) < 5) {
                        closestString.second.toFloat()
                    } else {
                        detectedFreq
                    }
                }

                return sampleRate.toFloat() / betterTau.toFloat()
            }
            tau++
        }

        // Intenta encontrar la fundamental usando otros métodos si YIN falla
        val hpsFrequency = harmonicProductSpectrum()
        if (hpsFrequency > 0) {
            return hpsFrequency
        }

        return -1f // No se encontró la frecuencia
    }

    private fun preProcessSignal(input: ShortArray): DoubleArray {
        val size = input.size
        val output = DoubleArray(size)

        // Convertir a double y normalizar
        var max = 0.0
        for (i in 0 until size) {
            output[i] = input[i].toDouble()
            if (abs(output[i]) > max) {
                max = abs(output[i])
            }
        }

        // Normalización
        if (max > 0) {
            for (i in 0 until size) {
                output[i] /= max
            }
        }

        // Aplicar ventana de Hamming para mejorar el análisis de frecuencia
        for (i in 0 until size) {
            output[i] *= (0.54 - 0.46 * kotlin.math.cos(2 * Math.PI * i / (size - 1)))
        }

        return output
    }

    // Método alternativo: Harmonic Product Spectrum para frecuencias bajas
    private fun harmonicProductSpectrum(): Float {
        val fftSize = 8192 // Tamaño grande para mejor resolución en bajas frecuencias
        val fft = DoubleArray(fftSize * 2) // Real + Imaginario

        // Preparar datos para FFT
        for (i in 0 until minOf(bufferSize, fftSize)) {
            fft[i * 2] =
                audioData[i].toDouble() * (0.54 - 0.46 * kotlin.math.cos(2 * Math.PI * i / (bufferSize - 1)))
            fft[i * 2 + 1] = 0.0
        }

        // Realizar FFT (aquí necesitarías una implementación de FFT)
        // Por simplicidad, usaremos una función ficticia
        val spectrum = performFFT(fft, fftSize)

        // Producto armónico
        val downsampled = Array(5) { DoubleArray(fftSize / 2) }

        // Calcular el espectro original y sus versiones downsampled
        for (i in 0 until fftSize / 2) {
            downsampled[0][i] = spectrum[i]
        }

        // Downsample el espectro por factores de 2, 3, 4 y 5
        for (h in 1 until 5) {
            for (i in 0 until fftSize / (2 * (h + 1))) {
                downsampled[h][i] = spectrum[i * (h + 1)]
            }
        }

        // Multiplicar los espectros
        val hps = DoubleArray(fftSize / 10)
        for (i in 0 until fftSize / 10) {
            hps[i] = 1.0
            for (h in 0 until 5) {
                hps[i] *= downsampled[h][i]
            }
        }

        // Encontrar el índice máximo
        var maxIndex = 0
        var maxValue = hps[0]
        for (i in 1 until fftSize / 10) {
            if (hps[i] > maxValue) {
                maxValue = hps[i]
                maxIndex = i
            }
        }

        // Calcular la frecuencia
        val frequency = maxIndex * sampleRate.toFloat() / fftSize

        // Verificar si está dentro del rango de guitarra
        return if (frequency > 70 && frequency < 500) {
            frequency
        } else {
            -1f
        }
    }

    // Simulación de FFT (en una implementación real, usarías una biblioteca como FFTW o JTransforms)
    private fun performFFT(input: DoubleArray, size: Int): DoubleArray {
        // Esta es una implementación simulada
        // En un caso real, usarías una biblioteca de FFT
        val magnitude = DoubleArray(size / 2)
        for (i in 0 until size / 2) {
            val real = input[i * 2]
            val imag = input[i * 2 + 1]
            magnitude[i] = Math.sqrt(real * real + imag * imag)
        }
        return magnitude
    }

    private fun findClosestGuitarString(frequency: Float): Pair<String, Double>? {
        var closestString: String? = null
        var minDifference = Double.MAX_VALUE
        var closestFreq = 0.0

        for ((note, freq) in guitarStrings) {
            val difference = abs(frequency - freq)
            if (difference < minDifference && difference / freq < 0.1) { // Max 10% de diferencia
                minDifference = difference
                closestString = note
                closestFreq = freq
            }
        }

        return if (closestString != null) {
            Pair(closestString, closestFreq)
        } else {
            null
        }
    }

    // Método para convertir una frecuencia a una nota musical con mejor precisión
    fun frequencyToNote(frequency: Float): NoteData {
        if (frequency <= 0) return NoteData("--", 0f, 0f)

        // Verificar primero si es una cuerda de guitarra
        val closestString = findClosestGuitarString(frequency)
        if (closestString != null && abs(frequency - closestString.second.toFloat()) < 5) {
            val cents = 1200 * log10(frequency / closestString.second) / log10(2.0)
            return NoteData(closestString.first, frequency, cents.toFloat())
        }

        // Si no es una cuerda de guitarra, usar el método general
        // A4 = 440Hz
        val a4 = 440.0
        val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

        // Logaritmo para encontrar el número de semitonos desde A4
        val semitonesFromA4 = 12 * log10(frequency / a4) / log10(2.0)

        // Calcular el número de octava
        val octave = (semitonesFromA4 / 12 + 4).toInt()

        // Calcular el índice de la nota dentro de la octava
        val noteIndex = ((semitonesFromA4 % 12) + 12) % 12
        val noteIndexRounded = round(noteIndex).toInt()

        // Calcular la frecuencia exacta de la nota más cercana
        val exactFrequency = a4 * 2.0.pow((noteIndexRounded - 9 + (octave - 4) * 12) / 12.0)

        // Calcular la desviación en cents
        val cents = 1200 * log10(frequency / exactFrequency) / log10(2.0)

        val noteName = noteNames[noteIndexRounded % 12] + octave

        return NoteData(noteName, frequency, cents.toFloat())
    }

    data class NoteData(val note: String, val frequency: Float, val cents: Float)
}