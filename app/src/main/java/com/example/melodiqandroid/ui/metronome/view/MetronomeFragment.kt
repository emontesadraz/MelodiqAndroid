package com.example.melodiqandroid.ui.metronome.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.melodiqandroid.R
import com.example.melodiqandroid.ui.metronome.viewmodel.MetronomeViewModel

class MetronomeFragment : Fragment() {

    private lateinit var metronomeViewModel: MetronomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_metronome, container, false)

        // Inicializa el ViewModel
        metronomeViewModel = ViewModelProvider(this).get(MetronomeViewModel::class.java)

        // Referencias a los elementos de la UI
        val bpmText = root.findViewById<TextView>(R.id.bpmText)
        val seekBar = root.findViewById<SeekBar>(R.id.seekBar)

        // Observa los cambios en el LiveData de BPM
        metronomeViewModel.bpm.observe(viewLifecycleOwner) { bpm ->
            bpmText.text = "BPM: $bpm"
            seekBar.progress = bpm
        }

        // Configura el SeekBar
        seekBar.max = MetronomeViewModel.MAX_BPM - MetronomeViewModel.MIN_BPM
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    metronomeViewModel.setBpm(progress + MetronomeViewModel.MIN_BPM)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        return root
    }
}