package com.example.melodiqandroid.ui.minigames.guessthesound.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.melodiqandroid.R
import com.example.melodiqandroid.databinding.FragmentMinigameBinding
import com.example.melodiqandroid.ui.minigames.guessthesound.model.Score
import com.example.melodiqandroid.ui.minigames.guessthesound.view.GameResultDialog
import com.example.melodiqandroid.ui.minigames.guessthesound.viewmodel.ChordGameViewModel
import com.example.melodiqandroid.ui.minigames.guessthesound.viewmodel.ChordOptionsAdapter
import com.example.melodiqandroid.ui.minigames.guessthesound.viewmodel.GameState
import com.example.melodiqandroid.ui.minigames.guessthesound.viewmodel.RoundResult
import com.google.android.material.snackbar.Snackbar

/**
 * Fragment principal para el juego de adivinar acordes
 */
class ChordGameFragment : Fragment() {

    private var _binding: FragmentMinigameBinding? = null
    private val binding get() = _binding!!

    // ViewModel del juego
    private val viewModel: ChordGameViewModel by viewModels()

    // Adaptador para las opciones de acordes
    private lateinit var chordAdapter: ChordOptionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMinigameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupButtons()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        chordAdapter = ChordOptionsAdapter(
            onChordSelected = { chord ->
                viewModel.selectChord(chord)
            },
            onPlaySampleRequested = { chord ->
                viewModel.playSampleChord(chord)
            }
        )

        binding.recyclerChordOptions.apply {
            adapter = chordAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupButtons() {
        // Botón para iniciar el juego
        binding.buttonStartGame.setOnClickListener {
            viewModel.startGame()
        }

        // Botón para reproducir el sonido actual
        binding.fabPlaySound.setOnClickListener {
            viewModel.playCurrentChord()
        }
    }

    private fun observeViewModel() {
        // Observar el estado del juego
        viewModel.gameState.observe(viewLifecycleOwner) { state ->
            updateUIForGameState(state)
        }

        // Observar las opciones de acordes
        viewModel.chordOptions.observe(viewLifecycleOwner) { options ->
            chordAdapter.submitList(options)
        }

        // Observar la información de la ronda
        viewModel.roundInfo.observe(viewLifecycleOwner) { info ->
            binding.textRound.text = "${info.currentRound}/${info.totalRounds}"
            binding.textScore.text = "${info.currentScore}"
        }

        // Observar el resultado de la ronda
        viewModel.roundResult.observe(viewLifecycleOwner) { result ->
            showRoundResult(result)
        }

        // Observar el resultado final del juego
        viewModel.gameResult.observe(viewLifecycleOwner) { result ->
            showGameResultDialog(result)
        }
    }

    private fun updateUIForGameState(state: GameState) {
        when (state) {
            is GameState.Loading -> {
                binding.loadingOverlay.visibility = View.VISIBLE
                binding.buttonStartGame.isEnabled = false
                binding.fabPlaySound.isEnabled = false
                chordAdapter.setInteractionsEnabled(false)
            }
            is GameState.Ready -> {
                binding.loadingOverlay.visibility = View.GONE
                binding.buttonStartGame.isEnabled = true
                binding.buttonStartGame.text = "Jugar"
                binding.fabPlaySound.isEnabled = false
                chordAdapter.setInteractionsEnabled(true)
            }
            is GameState.Playing -> {
                binding.loadingOverlay.visibility = View.GONE
                binding.buttonStartGame.isEnabled = false
                binding.buttonStartGame.text = "Jugando..."
                binding.fabPlaySound.isEnabled = true
                chordAdapter.setInteractionsEnabled(true)
                // Reproducir automáticamente el sonido al iniciar cada ronda
                viewModel.playCurrentChord()
            }
            is GameState.RoundFeedback -> {
                binding.fabPlaySound.isEnabled = false
                chordAdapter.setInteractionsEnabled(false)
            }
            is GameState.Finished -> {
                binding.loadingOverlay.visibility = View.GONE
                binding.buttonStartGame.isEnabled = true
                binding.buttonStartGame.text = "Jugar de nuevo"
                binding.fabPlaySound.isEnabled = false
                chordAdapter.setInteractionsEnabled(false)
            }
            is GameState.Error -> {
                binding.loadingOverlay.visibility = View.GONE
                binding.buttonStartGame.isEnabled = true
                binding.fabPlaySound.isEnabled = false
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
            }
            else -> { /* Estado no controlado */ }
        }
    }

    private fun showRoundResult(result: RoundResult) {
        val message = if (result.isCorrect) {
            "¡Correcto! +${result.points} puntos"
        } else {
            "Incorrecto. El acorde era ${result.correctChord.displayName}"
        }

        val backgroundColor = if (result.isCorrect)
            R.color.correct_answer
        else
            R.color.incorrect_answer

        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(resources.getColor(backgroundColor, null))
            .show()
    }

    private fun showGameResultDialog(result: Score) {
        GameResultDialog(
            result = result,
            onPlayAgain = { viewModel.startGame() },
            onReturn = { findNavController().navigateUp() }
        ).show(childFragmentManager, "game_result")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}