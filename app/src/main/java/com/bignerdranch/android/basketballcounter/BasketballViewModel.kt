package com.bignerdranch.android.basketballcounter

import androidx.lifecycle.ViewModel

class BasketballViewModel : ViewModel() {
    var scoreA:Int = 0
    var scoreB:Int = 0

    fun updatePoints(team: String, score: Int):Int{
        if (team == "A") {
            scoreA += score
            return scoreA
        } else {
            scoreB += score
            return scoreB
        }
    }

    fun resetScore(){
        scoreA = 0
        scoreB = 0
    }
}