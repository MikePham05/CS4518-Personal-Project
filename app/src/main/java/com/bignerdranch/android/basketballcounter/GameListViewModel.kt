package com.bignerdranch.android.basketballcounter

import android.util.Log
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime
import java.util.*

class GameListViewModel : ViewModel() {
    private val gameRepository = GameRepository.get()
    val gameListLiveData = gameRepository.getGames()

    init {
        for (i in 0 until 150) {
            var game = Game()
            game.id = UUID.randomUUID()
            game.teamAName = randomNameGenerator()
            game.teamBName = randomNameGenerator()
            game.teamAScore = (0..150).random()
            game.teamBScore = (0..150).random()
            game.date = Date()
            gameRepository.addGame(game)
        }
    }

    private fun randomNameGenerator(): String {
        val alphabet: List<Char> = ('A'..'Z') + ('0'..'9')
        return List(10) { alphabet.random() }.joinToString("")
    }
}