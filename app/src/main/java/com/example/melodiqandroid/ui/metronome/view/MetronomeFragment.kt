package com.example.melodiqandroid.ui.metronome.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

        // Configuración inicial del SeekBar
        binding.seekBar.max = MetronomeViewModel.MAX_BPM - MetronomeViewModel.MIN_BPM
        binding.seekBar.progress = viewModel.bpm.value!! - MetronomeViewModel.MIN_BPM

        // Actualiza el texto cuando cambia el ViewModel
        viewModel.bpm.observe(viewLifecycleOwner) { bpm ->
            binding.bpmText.text = "$bpm BPM"
            binding.seekBar.progress = bpm - MetronomeViewModel.MIN_BPM
        }

        // Configura el listener del SeekBar
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newBpm = progress + MetronomeViewModel.MIN_BPM
                    viewModel.setBpm(newBpm)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.startButton.setOnClickListener {
            viewModel.startMetronome()
        }

        binding.stopButton.setOnClickListener {
            viewModel.stopMetronome()
        }
        // Observador para el estado de reproducción
        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.stopButton.isEnabled = isPlaying
            binding.startButton.isEnabled = !isPlaying
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}