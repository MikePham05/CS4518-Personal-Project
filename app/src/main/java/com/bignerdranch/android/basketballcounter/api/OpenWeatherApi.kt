package com.bignerdranch.android.basketballcounter.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "2c081869eee1974c9879b98a855d70f4"
public const val CITY_NAME = "Worcester"
private const val UNITS = "Imperial"

interface OpenWeatherApi {
    @GET("weather?")
    fun fetchContents(@Query("q") q: String = CITY_NAME, @Query("appid") appid: String = API_KEY, @Query("units") units: String = UNITS): Call<OpenWeatherResponse>
}