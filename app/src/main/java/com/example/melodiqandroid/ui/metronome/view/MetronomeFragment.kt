package com.example.melodiqandroid.ui.metronome.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.melodiqandroid.databinding.FragmentMetronomeBinding
import com.example.melodiqandroid.ui.metronome.viewmodel.MetronomeViewModel

class MetronomeFragment : Fragment() {

    private var _binding: FragmentMetronomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MetronomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMetronomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración inicial
        binding.bpmText.text = "${viewModel.bpm.value} BPM"

        // Listeners
        binding.startButton.setOnClickListener {
            viewModel.startMetronome()
        }

        binding.stopButton.setOnClickListener {
            viewModel.stopMetronome()
        }

        // Observadores
        viewModel.bpm.observe(viewLifecycleOwner) { bpm ->
            binding.bpmText.text = "$bpm BPM"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}