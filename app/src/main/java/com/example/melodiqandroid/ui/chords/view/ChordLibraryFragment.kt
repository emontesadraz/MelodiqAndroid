package com.example.melodiqandroid.ui.chords.view

import com.example.melodiqandroid.ui.chords.model.Chord
import com.example.melodiqandroid.ui.chords.model.Note
import com.example.melodiqandroid.ui.chords.view.adapter.ChordAdapter
import com.example.melodiqandroid.ui.chords.viewmodel.ChordViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.melodiqandroid.R

class ChordLibraryFragment : Fragment() {

    private lateinit var viewModel: ChordViewModel
    private lateinit var chordAdapter: ChordAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var spinnerNoteFilter: Spinner
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chord_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.rvChords)
        spinnerNoteFilter = view.findViewById(R.id.spinnerNoteFilter)
        progressBar = view.findViewById(R.id.progressBar)

        // Configurar ViewModel
        val repository = ChordRepository()
        val factory = ChordViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory)[ChordViewModel::class.java]

        // Configurar RecyclerView
        setupRecyclerView()

        // Configurar filtro de notas
        setupNoteFilter()

        // Observar cambios en los datos
        observeViewModel()
    }

    private fun setupRecyclerView() {
        chordAdapter = ChordAdapter { chord ->
            // Al hacer clic en un acorde, navegamos al detalle
            navigateToChordDetail(chord)
        }

        recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = chordAdapter
        }
    }

    private fun setupNoteFilter() {
        // Obtener los nombres de las notas para el spinner
        val noteNames = Note.values().map { it.getDisplayName() }.toTypedArray()

        // Agregar "Todos" como primera opción
        val options = arrayOf("Todos") + noteNames

        // Configurar el adaptador del spinner
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            options
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerNoteFilter.adapter = spinnerAdapter

        // Configurar el listener del spinner
        spinnerNoteFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    // Opción "Todos"
                    viewModel.loadAllChords()
                } else {
                    // Filtrar por nota seleccionada
                    val selectedNote = Note.values()[position - 1]
                    viewModel.filterChordsByRoot(selectedNote)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }
    }

    private fun observeViewModel() {
        // Observar los acordes filtrados
        viewModel.filteredChords.observe(viewLifecycleOwner) { chords ->
            chordAdapter.submitList(chords)
        }

        // Observar el estado de carga
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observar errores
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToChordDetail(chord: Chord) {
        // Seleccionar el acorde en el ViewModel
        viewModel.selectChord(chord)

        // Usar Navigation Component para navegar
        findNavController().navigate(R.id.action_chord_library_to_chord_detail)

        // Si no estás usando Navigation Component, puedes seguir usando la navegación manual:
        /*
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, ChordDetailFragment())
            .addToBackStack(null)
            .commit()
        */
    }
}

// Factory para crear el ViewModel con dependencias
class ChordViewModelFactory(private val repository: ChordRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}