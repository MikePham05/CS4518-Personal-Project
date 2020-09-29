package com.bignerdranch.android.basketballcounter

import android.app.Application

class GameCounterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        GameRepository.initialize(this)
    }
}