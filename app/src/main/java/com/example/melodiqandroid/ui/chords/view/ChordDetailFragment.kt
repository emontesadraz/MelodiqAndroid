package com.example.melodiqandroid.ui.chords.view

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.melodiqandroid.R
import com.example.melodiqandroid.ui.chords.viewmodel.ChordViewModel

class ChordDetailFragment : Fragment() {

    private lateinit var viewModel: ChordViewModel
    private lateinit var tvChordName: TextView
    private lateinit var tvChordType: TextView
    private lateinit var imgChordDiagram: ImageView
    private lateinit var btnPlayChord: Button
    private lateinit var btnBack: Button
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chord_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        tvChordName = view.findViewById(R.id.tvChordName)
        tvChordType = view.findViewById(R.id.tvChordType)
        imgChordDiagram = view.findViewById(R.id.imgChordDiagram)
        btnPlayChord = view.findViewById(R.id.btnPlayChord)
        btnBack = view.findViewById(R.id.btnBack)

        // Obtener la instancia del ViewModel compartido
        viewModel = ViewModelProvider(requireActivity())[ChordViewModel::class.java]

        // Configurar el botón de volver
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Configurar el botón de reproducir
        btnPlayChord.setOnClickListener {
            // Aquí implementarás la reproducción del acorde
            viewModel.selectedChord.value?.let { chord ->
                playChordSound(chord.soundResourceId)
            }
        }

        // Observar el acorde seleccionado
        viewModel.selectedChord.observe(viewLifecycleOwner) { chord ->
            chord?.let {
                tvChordName.text = it.name
                tvChordType.text = it.type.toString()
                imgChordDiagram.setImageResource(it.diagramResourceId)
            }
        }
    }
    private fun playChordSound(soundResourceId: Int) {
        // Liberar recursos del reproductor anterior
        mediaPlayer?.release()

        // Crear nuevo reproductor y reproducir sonido
        mediaPlayer = MediaPlayer.create(requireContext(), soundResourceId)
        mediaPlayer?.setOnCompletionListener { it.release() }
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}