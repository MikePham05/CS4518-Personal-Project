package com.bignerdranch.android.basketballcounter.api

import retrofit2.Call
import retrofit2.http.GET

interface CornhubApi {
    @GET("/")
    fun fetchContents(): Call<String>
}