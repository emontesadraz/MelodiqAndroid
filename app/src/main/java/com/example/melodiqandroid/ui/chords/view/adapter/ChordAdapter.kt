package com.example.melodiqandroid.ui.chords.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.melodiqandroid.R
import com.example.melodiqandroid.ui.chords.model.Chord

class ChordAdapter(private val onChordClick: (Chord) -> Unit) :
    ListAdapter<Chord, ChordAdapter.ChordViewHolder>(ChordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chord, parent, false)
        return ChordViewHolder(view, onChordClick)
    }

    override fun onBindViewHolder(holder: ChordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChordViewHolder(
        itemView: View,
        private val onChordClick: (Chord) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvChordName: TextView = itemView.findViewById(R.id.tvChordName)
        private val tvChordType: TextView = itemView.findViewById(R.id.tvChordType)

        fun bind(chord: Chord) {
            tvChordName.text = chord.name
            tvChordType.text = chord.type.getDisplayName()

            itemView.setOnClickListener {
                onChordClick(chord)
            }
        }
    }
}

class ChordDiffCallback : DiffUtil.ItemCallback<Chord>() {
    override fun areItemsTheSame(oldItem: Chord, newItem: Chord): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Chord, newItem: Chord): Boolean {
        return oldItem == newItem
    }
}