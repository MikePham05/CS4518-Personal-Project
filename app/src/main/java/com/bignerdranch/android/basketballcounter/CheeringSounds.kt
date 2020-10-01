package com.bignerdranch.android.basketballcounter

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import java.io.IOException
import java.lang.NullPointerException

private const val TAG = "cheering"
private const val SOUNDS_FOLDER = "cheeringSounds"
private const val MAX_SOUNDS = 1

class CheeringSounds (private val assets: AssetManager){
    private val soundPool = SoundPool.Builder().build()
    val sounds: List<Sound> = loadSounds()

    private fun loadSounds(): List<Sound>{
        val soundNames: Array<String>
        try {
            soundNames = assets.list(SOUNDS_FOLDER)!!
        } catch (e: Exception) {
            Log.e(TAG, "Could not list assets", e)
            return emptyList()
        }
        val sounds = mutableListOf<Sound>()
        soundNames.forEach {filename ->
            val assetPath = "$SOUNDS_FOLDER/$filename"
            val sound = Sound(assetPath)
            try {
                Log.d(TAG, "${this.soundPool}")
                load(sound)
                sounds.add(sound)
            } catch (ioe: IOException){
                //Log.e(TAG, "Could not load sound $filename", ioe)
                Log.d(TAG, "01")
            }
        }
        return sounds
    }

    private fun load(sound: Sound) {
        val afd: AssetFileDescriptor = assets.openFd(sound.assetPath)
        val soundId = soundPool.load(afd, 1)
        sound.soundId = soundId
    }


    fun play(sound: Sound) {
        sound.soundId?.let {
            soundPool.play(it, 1.0f, 1.0f, 1, 0 ,1.0f)
        }
    }
}