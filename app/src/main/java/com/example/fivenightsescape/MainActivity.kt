package com.example.fivenightsescape

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_layout)

        val difficultyOptions = findViewById<RadioGroup>(R.id.difficultyOptions)

        difficultyOptions.setOnCheckedChangeListener { group, checkedId ->
            val selectedDifficulty = when (checkedId) {
                R.id.easy -> "Easy"
                R.id.medium -> "Medium"
                R.id.hard -> "Hard"
                else -> "Unknown"
            }

        val startButton: Button = findViewById(R.id.startButton)

        startButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("selectedDifficulty", selectedDifficulty)
            startActivity(intent)
        }
        }
    }
}
