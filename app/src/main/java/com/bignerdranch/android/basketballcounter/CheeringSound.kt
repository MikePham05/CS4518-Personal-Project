package com.bignerdranch.android.basketballcounter

import android.content.res.AssetManager
import android.util.Log

private const val TAG = "Cheering"
private const val SOUNDS_FOLDER = "cheering_sounds"

class CheeringSound(private val assets: AssetManager) {

    fun loadSounds(): List<String> {
        try {
            val soundNames = assets.list(SOUNDS_FOLDER)!!
            Log.d(TAG, "Found ${soundNames.size} sounds")
            return soundNames.asList()
        } catch (e: Exception) {
            Log.e(TAG, "Could not list assets", e)
            return emptyList()
        }
    }
}