package com.example.kuba

object Model {
    data class Result(val weather: List<Weather>,val main: Main,val sys:Sys,val dt:String,val name: String)
    data class Weather(val main: String,val description: String)
    data class Main(val temp: String,val pressure: String,val humidity: String)
    data class Sys(val sunrise: String,val sunset: String)
}
