package com.bignerdranch.android.basketballcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
private const val KEY_SCORE_A = "scoreA"
private const val KEY_SCORE_B = "scoreB"


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var plus3TeamAButton: Button
    private lateinit var plus2TeamAButton: Button
    private lateinit var plus1TeamAButton: Button
    private lateinit var plus3TeamBButton: Button
    private lateinit var plus2TeamBButton: Button
    private lateinit var plus1TeamBButton: Button
    private lateinit var resetButton: Button
    private lateinit var scoreATextView: TextView
    private lateinit var scoreBTextView: TextView
    private var startTime:Long = 0
    private var endTime:Long = 0

    private val basketballViewModel: BasketballViewModel by lazy {
        ViewModelProviders.of(this).get(BasketballViewModel::class.java)
    }


    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(KEY_SCORE_A, basketballViewModel.scoreA)
        savedInstanceState.putInt(KEY_SCORE_B, basketballViewModel.scoreB)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        startTime = System.currentTimeMillis()
        Log.d(TAG, "The activity has started")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        plus3TeamAButton = findViewById(R.id.plus3_button_teamA)
        plus2TeamAButton = findViewById(R.id.plus2_button_teamA)
        plus1TeamAButton = findViewById(R.id.plus1_button_teamA)
        plus3TeamBButton = findViewById(R.id.plus3_button_teamB)
        plus2TeamBButton = findViewById(R.id.plus2_button_teamB)
        plus1TeamBButton = findViewById(R.id.plus1_button_teamB)
        resetButton = findViewById(R.id.reset_button)
        scoreATextView = findViewById(R.id.team_a_score)
        scoreBTextView = findViewById(R.id.team_b_score)
        scoreATextView.text = basketballViewModel.scoreA.toString()
        scoreBTextView.text = basketballViewModel.scoreB.toString()

        plus3TeamAButton.setOnClickListener {
            scoreATextView.text = (basketballViewModel.updatePoints("A", 3).toString())
        }

        plus2TeamAButton.setOnClickListener{
            scoreATextView.text = (basketballViewModel.updatePoints("A", 2).toString())
        }

        plus1TeamAButton.setOnClickListener{
            scoreATextView.text = (basketballViewModel.updatePoints("A", 1).toString())
        }

        plus3TeamBButton.setOnClickListener{
            scoreBTextView.text = (basketballViewModel.updatePoints("B", 3).toString())
        }

        plus2TeamBButton.setOnClickListener{
            scoreBTextView.text = (basketballViewModel.updatePoints("B", 2).toString())
        }

        plus1TeamBButton.setOnClickListener{
            scoreBTextView.text = (basketballViewModel.updatePoints("B", 1).toString())
        }

        resetButton.setOnClickListener{
            basketballViewModel.resetScore()
            scoreATextView.text = "0"
            scoreBTextView.text = "0"
        }
    }


    override fun onDestroy() {
        endTime = System.currentTimeMillis()
        val runtime = ((endTime - startTime).toFloat())/1000
        Log.d(TAG, "The activity has been destroyed after running for $runtime seconds")
        super.onDestroy()
    }


}

