package com.example.melodiqandroid.ui.tuner.view

import android.Manifest
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.melodiqandroid.databinding.FragmentTunerBinding
import com.example.melodiqandroid.ui.tuner.viewmodel.TunerViewModel
import kotlin.math.abs

class TunerFragment : Fragment() {

    private var _binding: FragmentTunerBinding? = null
    private val binding get() = _binding!!

    private lateinit var tunerViewModel: TunerViewModel
    private var needleAnimator: ValueAnimator? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startTuner()
        } else {
            Toast.makeText(
                requireContext(),
                "El permiso de micrófono es necesario para el afinador",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTunerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tunerViewModel = ViewModelProvider(this)[TunerViewModel::class.java]

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        tunerViewModel.noteData.observe(viewLifecycleOwner) { noteData ->
            // Extraer nombre de nota y octava
            val noteName = noteData.note.replace("\\d".toRegex(), "")
            val octave = noteData.note.replace("\\D".toRegex(), "")

            binding.txtNote.text = noteName
            binding.txtOctave.text = octave
            binding.txtFrequency.text = String.format("%.1f Hz", noteData.frequency)
            binding.txtCents.text = "${noteData.cents.toInt()}¢"

            // Calcular la rotación de la aguja basada en los cents
            // Los cents varían de -50 a +50, y queremos que la aguja rote aproximadamente 90 grados
            val rotationDegrees = (noteData.cents * 1.8f).coerceIn(-90f, 90f)

            // Animar la aguja suavemente
            animateNeedle(rotationDegrees)

            // Cambiar el color de la línea central según la afinación
            if (abs(noteData.cents) < 5) {
                binding.centerLine.setBackgroundColor(Color.parseColor("#00AA00")) // Verde
                binding.txtCents.setTextColor(Color.parseColor("#00AA00"))
            } else {
                binding.centerLine.setBackgroundColor(Color.parseColor("#000000")) // Negro
                binding.txtCents.setTextColor(Color.parseColor("#444444"))
            }
        }

        tunerViewModel.isListening.observe(viewLifecycleOwner) { isListening ->
            binding.btnToggleTuner.text = if (isListening) "Detener" else "Iniciar"
        }
    }

    private fun animateNeedle(targetRotation: Float) {
        needleAnimator?.cancel()

        val currentRotation = binding.tunerNeedle.rotation
        needleAnimator = ValueAnimator.ofFloat(currentRotation, targetRotation).apply {
            duration = 150 // Duración corta para que se sienta responsive
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                binding.tunerNeedle.rotation = animation.animatedValue as Float
            }
            start()
        }
    }

    private fun setupClickListeners() {
        binding.btnToggleTuner.setOnClickListener {
            if (tunerViewModel.isListening.value == true) {
                tunerViewModel.stopListening()
            } else {
                checkMicPermission()
            }
        }
    }

    private fun checkMicPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startTuner()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                Toast.makeText(
                    requireContext(),
                    "El afinador necesita acceso al micrófono para funcionar",
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startTuner() {
        tunerViewModel.startListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        needleAnimator?.cancel()
        needleAnimator = null
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        tunerViewModel.stopListening()
    }
}