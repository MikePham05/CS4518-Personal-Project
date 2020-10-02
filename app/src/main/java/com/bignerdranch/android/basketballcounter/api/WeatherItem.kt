package com.bignerdranch.android.basketballcounter.api

import com.google.gson.annotations.SerializedName

data class WeatherItem(
    @SerializedName("temp") var temp: Float
)