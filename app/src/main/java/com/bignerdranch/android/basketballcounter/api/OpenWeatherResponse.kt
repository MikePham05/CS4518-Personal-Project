package com.bignerdranch.android.basketballcounter.api

import com.google.gson.annotations.SerializedName

class OpenWeatherResponse {
    @SerializedName("main")
    lateinit var main: WeatherItem
}