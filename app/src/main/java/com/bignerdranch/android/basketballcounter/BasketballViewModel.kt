package com.bignerdranch.android.basketballcounter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class BasketballViewModel : ViewModel() {
    private val gameRepository = GameRepository.get()
    private val gameIdLiveData = MutableLiveData<UUID>()
    var teamAName = "Team A"
    var teamBName = "Team B"
    var gameLiveData: LiveData<Game> =
        Transformations.switchMap(gameIdLiveData) { gameId ->
            gameRepository.getGame(gameId)
        }
    var scoreA: Int = 0
    var scoreB: Int = 0
    var scoreDifference: Int = 0

    fun updatePoints(team: String, score: Int): Int {
        if (team == "A") {
            gameLiveData.value!!.teamAScore += score
            scoreA = gameLiveData.value!!.teamAScore
            return gameLiveData.value!!.teamAScore
        } else {
            gameLiveData.value!!.teamBScore += score
            scoreB = gameLiveData.value!!.teamBScore
            return gameLiveData.value!!.teamBScore
        }
    }

    fun resetScore() {
        scoreA = 0
        scoreB = 0
    }

    fun loadGame(gameId: UUID) {
        gameIdLiveData.value = gameId
        Log.d("idd", "${gameIdLiveData.value}")
    }

    fun saveGame(game: Game){
        gameRepository.updateGame(game)
    }

    fun getPhotoFile(game: Game): File {
        return gameRepository.getPhotoFile(game)
    }
}
