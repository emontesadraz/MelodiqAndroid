package com.example.melodiqandroid.ui.tuner.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            binding.txtNote.text = noteData.note
            binding.txtCents.text = "${noteData.cents.toInt()} cents"

            // Actualizar el medidor (progress bar)
            // Convertimos los cents (-50 a +50) a un valor de progreso (0 a 100)
            val progressValue = ((noteData.cents + 50) * 100) / 100
            binding.tunerMeter.progress = progressValue.toInt().coerceIn(0, 100)

            // Cambiamos el color según qué tan afinado está
            if (abs(noteData.cents) < 5) {
                binding.txtNote.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
            } else {
                binding.txtNote.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }
        }

        tunerViewModel.isListening.observe(viewLifecycleOwner) { isListening ->
            binding.btnToggleTuner.text = if (isListening) "Detener" else "Iniciar"
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
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        tunerViewModel.stopListening()
    }
}