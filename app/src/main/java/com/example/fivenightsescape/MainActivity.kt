package com.example.fivenightsescape

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast

private const val LOG_TAG = "MainActivity"
private const val LOG_ERROR_MESSAGE = "Unexpected difficulty option selected"
private const val UNSELECTED_DIFFICULTY_MESSAGE = "Please select a difficulty level"
private const val INTENT_EXTRA = "selectedDifficulty"
private const val EASY = "Easy"
private const val MEDIUM = "Medium"
private const val HARD = "Hard"

class MainActivity : AppCompatActivity() {
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_layout)

        val difficultyOptions = findViewById<RadioGroup>(R.id.difficultyOptions)

        difficultyOptions.setOnCheckedChangeListener { _, checkedId ->
            val selectedDifficulty = getSelectedDifficulty(checkedId, difficultyOptions)
            setupStartButton(selectedDifficulty)
        }

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null && e1.x < e2.x) {
                    val selectedDifficulty = getSelectedDifficulty(difficultyOptions.checkedRadioButtonId, difficultyOptions)
                    startMapsActivity(selectedDifficulty)
                }
                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private fun getSelectedDifficulty(checkedId: Int, difficultyOptions: RadioGroup): String? {
        return when (checkedId) {
            R.id.easy -> EASY
            R.id.medium -> MEDIUM
            R.id.hard -> HARD
            else -> logError(difficultyOptions)
        }
    }

    private fun setupStartButton(selectedDifficulty: String?) {
        val startButton: Button = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            if (selectedDifficulty != null) {
                startMapsActivity(selectedDifficulty)
            } else {
                toastError()
            }
        }
    }

    private fun startMapsActivity(selectedDifficulty: String?) {
        if (selectedDifficulty != null) {
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            intent.putExtra(INTENT_EXTRA, selectedDifficulty)
            startActivity(intent)
        } else {
            toastError()
        }
    }

    private fun logError(difficultyOptions: RadioGroup) : String? {
        val message = "$LOG_ERROR_MESSAGE ${difficultyOptions.checkedRadioButtonId}"
        Log.w(LOG_TAG, message)
        return null
    }

    private fun toastError() {
        Toast.makeText(this@MainActivity, UNSELECTED_DIFFICULTY_MESSAGE, Toast.LENGTH_SHORT).show()
    }
}
