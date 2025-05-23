package com.example.melodiqandroid.ui.minigames.guessthesound.viewmodel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.melodiqandroid.R
import com.example.melodiqandroid.ui.minigames.guessthesound.model.Chord


/**
 * Adaptador para mostrar las opciones de acordes en el RecyclerView
 */
class ChordOptionsAdapter(
    private val onChordSelected: (Chord) -> Unit,
    private val onPlaySampleRequested: (Chord) -> Unit
) : ListAdapter<Chord, ChordOptionsAdapter.ChordViewHolder>(ChordDiffCallback()) {

    // Flag para deshabilitar interacciones durante feedback
    private var interactionsEnabled = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chord_option, parent, false)
        return ChordViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChordViewHolder, position: Int) {
        val chord = getItem(position)
        holder.bind(chord, interactionsEnabled)
    }

    /**
     * Habilita o deshabilita las interacciones con el adaptador
     */
    fun setInteractionsEnabled(enabled: Boolean) {
        interactionsEnabled = enabled
        notifyDataSetChanged()
    }

    inner class ChordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textChordName: TextView = itemView.findViewById(R.id.text_chord_name)
        private val buttonPlaySample: ImageButton = itemView.findViewById(R.id.button_play_sample)

        fun bind(chord: Chord, interactionsEnabled: Boolean) {
            textChordName.text = "${chord.displayName} (${chord.notation})"

            // Configurar el botón para reproducir la muestra
            buttonPlaySample.setOnClickListener {
                if (interactionsEnabled) {
                    onPlaySampleRequested(chord)
                }
            }

            // Configurar el clic en el elemento para seleccionar
            itemView.setOnClickListener {
                if (interactionsEnabled) {
                    onChordSelected(chord)
                }
            }

            // Actualizar estado visual
            itemView.isEnabled = interactionsEnabled
            buttonPlaySample.isEnabled = interactionsEnabled

            // Ajustar opacidad si está deshabilitado
            val alpha = if (interactionsEnabled) 1.0f else 0.5f
            itemView.alpha = alpha
        }
    }
}

/**
 * Clase de utilidad para calcular diferencias entre listas de acordes
 */
class ChordDiffCallback : DiffUtil.ItemCallback<Chord>() {
    override fun areItemsTheSame(oldItem: Chord, newItem: Chord): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Chord, newItem: Chord): Boolean {
        return oldItem == newItem
    }
}