package com.bignerdranch.android.basketballcounter

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.basketballcounter.database.GameDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "game-database"

class GameRepository private constructor(context: Context){
    private val database: GameDatabase = Room.databaseBuilder(
        context.applicationContext,
        GameDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val gameDao = database.gameDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getGames(): LiveData<List<Game>> = gameDao.getGames()

    fun getGame(id: UUID): LiveData<Game> = gameDao.getGame(id)

    fun updateGame(game: Game) {
        executor.execute{
            gameDao.updateGame(game)
        }
    }

    fun addGame(game: Game) {
        executor.execute{
            gameDao.addGame(game)
        }
    }


    companion object {
        private var INSTANCE: GameRepository?= null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = GameRepository(context)
            }
        }

        fun get(): GameRepository {
            return INSTANCE ?:
            throw IllegalStateException("must be initialized first")
        }
    }
}