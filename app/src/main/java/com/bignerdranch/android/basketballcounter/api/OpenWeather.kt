package com.bignerdranch.android.basketballcounter.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "OpenWeather"

class OpenWeather {
    private val openWeatherApi: OpenWeatherApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        openWeatherApi = retrofit.create(OpenWeatherApi::class.java)
    }

    fun fetchContents(): LiveData<String> {
        var responseLiveData: MutableLiveData<String> = MutableLiveData()
        val openWeatherRequest: Call<OpenWeatherResponse> = openWeatherApi.fetchContents()
        openWeatherRequest.enqueue(object : Callback<OpenWeatherResponse> {
            override fun onFailure(call: Call<OpenWeatherResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
            }
            override fun onResponse(
                call: Call<OpenWeatherResponse>,
                response: Response<OpenWeatherResponse>
            ) {
                Log.d(TAG, "Response received")
                val openWeatherResponse: OpenWeatherResponse? = response.body()
                val weatherItem: WeatherItem? = openWeatherResponse?.main
                responseLiveData.value = weatherItem?.temp.toString()
            }
        })
        return responseLiveData
    }
}