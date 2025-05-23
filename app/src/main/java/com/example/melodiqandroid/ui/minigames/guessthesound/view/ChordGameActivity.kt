package com.example.melodiqandroid.ui.minigames.guessthesound.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.melodiqandroid.R
import com.example.melodiqandroid.databinding.ActivityMinigameBinding


class ChordGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMinigameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMinigameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Si no hay fragmento, añadirlo
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ChordGameFragment())
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}