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

        /**
         * TunerFragment es un Fragment que implementa la interfaz de usuario para un afinador de instrumentos musicales.
         * Proporciona detección de notas en tiempo real, animaciones para la aguja del afinador y gestión de permisos de micrófono.
         */
        class TunerFragment : Fragment() {

            // ViewBinding para acceder a los elementos de la vista
            private var _binding: FragmentTunerBinding? = null
            private val binding get() = _binding!!

            // ViewModel que maneja la lógica de detección de notas
            private lateinit var tunerViewModel: TunerViewModel

            // Animador para la rotación suave de la aguja
            private var needleAnimator: ValueAnimator? = null

            // Lanzador para gestionar la solicitud de permisos de micrófono
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

            /**
             * Infla el layout del fragmento y configura el ViewBinding.
             */
            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View {
                _binding = FragmentTunerBinding.inflate(inflater, container, false)
                return binding.root
            }

            /**
             * Configura el ViewModel, observadores y listeners después de que la vista ha sido creada.
             */
            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                tunerViewModel = ViewModelProvider(this)[TunerViewModel::class.java]

                setupObservers()
                setupClickListeners()
            }

            /**
             * Configura los observadores para actualizar la interfaz de usuario en respuesta a los cambios en los datos del ViewModel.
             */
            private fun setupObservers() {
                tunerViewModel.noteData.observe(viewLifecycleOwner) { noteData ->
                    // Extraer nombre de nota y octava
                    val noteName = noteData.note.replace("\\d".toRegex(), "")
                    val octave = noteData.note.replace("\\D".toRegex(), "")

                    // Actualizar los textos de la interfaz
                    binding.txtNote.text = noteName
                    binding.txtOctave.text = octave
                    binding.txtFrequency.text = String.format("%.1f Hz", noteData.frequency)
                    binding.txtCents.text = "${noteData.cents.toInt()}¢"

                    // Calcular la rotación de la aguja basada en los cents
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

                // Observador para actualizar el texto del botón según el estado de escucha
                tunerViewModel.isListening.observe(viewLifecycleOwner) { isListening ->
                    binding.btnToggleTuner.text = if (isListening) "Detener" else "Iniciar"
                }
            }

            /**
             * Anima la rotación de la aguja del afinador hacia la posición objetivo.
             * @param targetRotation Rotación objetivo en grados.
             */
            private fun animateNeedle(targetRotation: Float) {
                needleAnimator?.cancel()

                val currentRotation = binding.tunerNeedle.rotation
                needleAnimator = ValueAnimator.ofFloat(currentRotation, targetRotation).apply {
                    duration = 150 // Duración corta para que se sienta responsivo
                    interpolator = DecelerateInterpolator()
                    addUpdateListener { animation ->
                        binding.tunerNeedle.rotation = animation.animatedValue as Float
                    }
                    start()
                }
            }

            /**
             * Configura los listeners para los clics en los botones de la interfaz.
             */
            private fun setupClickListeners() {
                binding.btnToggleTuner.setOnClickListener {
                    if (tunerViewModel.isListening.value == true) {
                        tunerViewModel.stopListening()
                    } else {
                        checkMicPermission()
                    }
                }
            }

            /**
             * Verifica si el permiso de micrófono ha sido concedido y lo solicita si es necesario.
             */
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

            /**
             * Inicia la escucha del afinador a través del ViewModel.
             */
            private fun startTuner() {
                tunerViewModel.startListening()
            }

            /**
             * Limpia los recursos y referencias cuando la vista es destruida.
             */
            override fun onDestroyView() {
                super.onDestroyView()
                needleAnimator?.cancel()
                needleAnimator = null
                _binding = null
            }

            /**
             * Detiene la escucha del afinador cuando el fragmento no está visible.
             */
            override fun onPause() {
                super.onPause()
                tunerViewModel.stopListening()
            }
        }