package com.example.kuba

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather?")
    fun hitCity(@Query("q") city: String,
                      @Query("appid") appid: String,
                      @Query("units") units: String,
                      @Query("lang") lang: String): Observable<Model.Result>
    @GET("weather?")
    fun hitId(@Query("id") id: String,
                @Query("appid") appid: String,
                @Query("units") units: String,
                @Query("lang") lang: String): Observable<Model.Result>

    @GET("weather?")
    fun hitCoords(@Query("lat") lat: String,
                  @Query("lon") lon: String,
                @Query("appid") appid: String,
                @Query("units") units: String,
                @Query("lang") lang: String): Observable<Model.Result>

    @GET("weather?")
    fun hitZIP(@Query("zip") zip: String,
                @Query("appid") appid: String,
                @Query("units") units: String,
                @Query("lang") lang: String): Observable<Model.Result>


    companion object {
        fun create(): WeatherApiService {

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .build()

            return retrofit.create(WeatherApiService::class.java)
        }
    }

}