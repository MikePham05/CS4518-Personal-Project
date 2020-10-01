package com.bignerdranch.android.basketballcounter

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*


private const val KEY_SCORE_A = "scoreA"
private const val KEY_SCORE_B = "scoreB"
private const val REQUEST_CODE_0 = 0
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), BasketballFragment.Callbacks, GameListFragment.CallBacks {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container);

        if (currentFragment == null) {
            val fragment = GameListFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }

        var cheeringSound = CheeringSound(assets)
        cheeringSound.loadSounds()

    }

    override fun onDisplayClicked() {
        val fragment = GameListFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        Log.d("testDetailToList", "List Fragment loaded")
    }

    override fun onGameSelected(gameId: UUID) {
        val fragment = BasketballFragment.newInstance(gameId)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        Log.d("TestListToDetail", "Game Fragment loaded")
    }
}


