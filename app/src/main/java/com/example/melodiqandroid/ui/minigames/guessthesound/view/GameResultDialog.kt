package com.example.melodiqandroid.ui.minigames.guessthesound.view

 import android.app.Dialog
 import android.os.Bundle
 import android.view.LayoutInflater
 import android.view.View
 import android.view.ViewGroup
 import androidx.fragment.app.DialogFragment
 import com.example.melodiqandroid.R
 import com.example.melodiqandroid.databinding.DialogGameResultBinding
 import com.example.melodiqandroid.ui.minigames.guessthesound.model.Score

/**
  * Diálogo que muestra el resultado final del juego
  */
 class GameResultDialog(
     private val result: Score,
     private val onPlayAgain: () -> Unit,
     private val onReturn: () -> Unit
 ) : DialogFragment() {

     private var _binding: DialogGameResultBinding? = null
     private val binding get() = _binding!!

     override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
         // Usar un dialog personalizado en lugar del estándar
         return Dialog(requireContext(), R.style.Theme_AppCompat_Dialog_MinWidth)
     }

     override fun onCreateView(
         inflater: LayoutInflater,
         container: ViewGroup?,
         savedInstanceState: Bundle?
     ): View {
         _binding = DialogGameResultBinding.inflate(inflater, container, false)
         return binding.root
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)

         // Configurar título según el resultado
         val successPercentage = result.getSuccessPercentage()
         binding.textResultTitle.text = when {
             successPercentage >= 80 -> "¡Excelente trabajo!"
             successPercentage >= 60 -> "¡Buen trabajo!"
             successPercentage >= 40 -> "¡Sigue practicando!"
             else -> "Necesitas más práctica"
         }

         // Configurar la imagen según el resultado
         binding.imageResult.setImageResource(
             when {
                 successPercentage >= 80 -> R.drawable.ic_result_excellent
                 successPercentage >= 60 -> R.drawable.ic_result_good
                 successPercentage >= 40 -> R.drawable.ic_result_ok
                 else -> R.drawable.ic_result_bad
             }
         )

         // Mostrar puntuación
         binding.textFinalScore.text = "${result.correctAnswers}/${result.totalRounds}"

         // Mostrar mensaje de feedback
         binding.textMessage.text = result.getFeedbackMessage()

         // Configurar botones
         binding.buttonPlayAgain.setOnClickListener {
             dismiss()
             onPlayAgain()
         }

         binding.buttonReturn.setOnClickListener {
             dismiss()
             onReturn()
         }
     }

     override fun onStart() {
         super.onStart()
         // Hacer que el diálogo ocupe casi toda la pantalla
         dialog?.window?.setLayout(
             ViewGroup.LayoutParams.MATCH_PARENT,
             ViewGroup.LayoutParams.WRAP_CONTENT
         )
     }

     override fun onDestroyView() {
         super.onDestroyView()
         _binding = null
     }
 }