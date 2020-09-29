package com.bignerdranch.android.basketballcounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

private const val TEAM_A_SCORE = "com.bignerdranch.android.basketballcounter.team_a_score"
private const val TEAM_B_SCORE = "com.bignerdranch.android.basketballcounter.team_b_score"
const val EXTRA_SCORE_DIFF = "com.bignerdranch.android.basketballcounter.score_diff"
private const val TAG = "MainActivity2"

class MainActivity2 : AppCompatActivity() {
    private lateinit var scoreDifference: TextView
    private lateinit var goBackButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        scoreDifference = findViewById(R.id.difference)
        goBackButton = findViewById(R.id.go_back_button)

        var team_a_score = intent.getIntExtra(TEAM_A_SCORE, 0)
        var team_b_score = intent.getIntExtra(TEAM_B_SCORE, 0)
        Log.d(TAG, "Data transferred from Main to Main2. TeamA: $team_a_score, teamB: $team_b_score")
        scoreDifference.text = (team_a_score - team_b_score).toString()


        goBackButton.setOnClickListener{
            setScoreDifferenceShown(team_a_score - team_b_score)
        }
    }

    private fun setScoreDifferenceShown(scoreDifference: Int) {
        var intent = Intent().apply{
            putExtra(EXTRA_SCORE_DIFF, scoreDifference)
        }
        setResult(Activity.RESULT_OK, intent)
        goBackButton.isEnabled = false
    }

    companion object {
        fun newIntent(packageContext: Context, teamAScore: Int, teamBScore: Int): Intent {
            return Intent(packageContext, MainActivity2::class.java).apply{
                putExtra(TEAM_A_SCORE, teamAScore)
                putExtra(TEAM_B_SCORE, teamBScore)
            }
        }
    }
}