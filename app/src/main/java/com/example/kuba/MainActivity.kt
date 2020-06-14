package com.example.kuba

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.kuba.ui.WeatherDetails
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.weather_fragment.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    private val wikiApiServe by lazy {
        WeatherApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_search_miejscowosc.setOnClickListener {
            if (edit_search_miejscowosc.text.toString().isNotEmpty()) {
                beginSearchCity(edit_search_miejscowosc.text.toString())
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(btn_search_miejscowosc.windowToken, 0)
            }
        }

        btn_search_id.setOnClickListener {
            if (edit_search_id.text.toString().isNotEmpty()) {
                beginSearchId(edit_search_id.text.toString())
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(btn_search_id.windowToken, 0)
            }
        }

        btn_search_koordynaty.setOnClickListener {
            if (edit_search_koordynaty_dlugosc.text.toString().isNotEmpty() && edit_search_koordynaty_szerokosc.text.toString().isNotEmpty()) {
                beginSearchCoords(
                    edit_search_koordynaty_dlugosc.text.toString(),
                    edit_search_koordynaty_szerokosc.text.toString()
                )
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(btn_search_koordynaty.windowToken, 0)
            }
        }

        btn_search_kod_pocztowy.setOnClickListener {
            if (edit_search_kod_pocztowy.text.toString().isNotEmpty()) {
                beginSearchZIP(edit_search_kod_pocztowy.text.toString())
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(btn_search_kod_pocztowy.windowToken, 0)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun beginSearchZIP(searchString: String) {
        disposable = wikiApiServe.hitZIP(
            searchString,
            "b9a31dcb2d2b84843ea2eba6b48ff5f9",
            "metric",
            "pl"
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    weather_city.text = "${result.name}"
                    weather_temp.text = "${result.main.temp.toInt()} °C"
                    tempDes.text = "Temperatura"
                    weather_pressure.text = "${result.main.pressure.toInt()} hPa"
                    pressureDes.text = "Ciśnienie"
                    weather_description.text = "${result.weather[0].description}"
//                    date.text = "${java.time.format.DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.ofEpochSecond(result.dt.toLong()))}"
                    weather_date.text = "${getDate(result.dt.toLong() * 1000, "dd MM yyyy")}"
                    if (result.weather[0].main == "Clear") {
                        icon.setBackgroundResource(R.drawable.ic_wb_sunny_black_24dp)
                    } else if (result.weather[0].main == "Rainy") {
                        icon.setBackgroundResource(R.drawable.ic_rain)
                    } else if (result.weather[0].main == "Windy") {
                        icon.setBackgroundResource(R.drawable.ic_icons8_winter_50)
                    } else if (result.weather[0].main == "Clouds") {
                        icon.setBackgroundResource(R.drawable.ic_clouds)
                    }
                    sunrise.text = "Wschód słońca: ${getTime(result.sys.sunrise.toLong() * 1000)}"
                    sunset.text = "Zachód słońca: ${getTime(result.sys.sunset.toLong() * 1000)}"

                    val intent = Intent(this, WeatherDetails::class.java).apply {}
                    this.startActivity(intent)
                },
                { error ->
                    Toast.makeText(
                        this,
                        "Wystąpił błąd. Sprawdź połączenie z internetem/poprawność danych",
                        Toast.LENGTH_LONG
                    ).show()
                } //error.message
            )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun beginSearchCoords(searchStringSzerokosc: String, searchStringDlugosc: String) {
        disposable = wikiApiServe.hitCoords(
            searchStringSzerokosc,
            searchStringDlugosc,
            "b9a31dcb2d2b84843ea2eba6b48ff5f9",
            "metric",
            "pl"
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    city.text = "${result.name}"
                    temp.text = "${result.main.temp} °C"
                    tempDes.text = "Temperatura"
                    pressure.text = "${result.main.pressure} hPa"
                    pressureDes.text = "Ciśnienie"
                    description.text = "${result.weather[0].description}"
//                    date.text = "${java.time.format.DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.ofEpochSecond(result.dt.toLong()))}"
                    date.text = "${getDate(result.dt.toLong() * 1000, "dd MM yyyy")}"
                    if (result.weather[0].main == "Clear") {
                        icon.setBackgroundResource(R.drawable.ic_wb_sunny_black_24dp)
                    } else if (result.weather[0].main == "Rainy") {
                        icon.setBackgroundResource(R.drawable.ic_rain)
                    } else if (result.weather[0].main == "Windy") {
                        icon.setBackgroundResource(R.drawable.ic_icons8_winter_50)
                    } else if (result.weather[0].main == "Clouds") {
                        icon.setBackgroundResource(R.drawable.ic_clouds)
                    }
                    sunrise.text = "Wschód słońca: ${getTime(result.sys.sunrise.toLong() * 1000)}"
                    sunset.text = "Zachód słońca: ${getTime(result.sys.sunset.toLong() * 1000)}"
                }
                ,
                { error ->
                    Toast.makeText(
                        this,
                        "Wystąpił błąd. Sprawdź połączenie z internetem/poprawność danych",
                        Toast.LENGTH_LONG
                    ).show()
                } //error.message
            )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun beginSearchId(searchString: String) {
        disposable = wikiApiServe.hitId(
            searchString,
            "b9a31dcb2d2b84843ea2eba6b48ff5f9",
            "metric",
            "pl"
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    city.text = "${result.name}"
                    temp.text = "${result.main.temp} °C"
                    tempDes.text = "Temperatura"
                    pressure.text = "${result.main.pressure} hPa"
                    pressureDes.text = "Ciśnienie"
                    description.text = "${result.weather[0].description}"
//                    date.text = "${java.time.format.DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.ofEpochSecond(result.dt.toLong()))}"
                    date.text = "${getDate(result.dt.toLong() * 1000, "dd MM yyyy")}"
                    if (result.weather[0].main == "Clear") {
                        icon.setBackgroundResource(R.drawable.ic_wb_sunny_black_24dp)
                    } else if (result.weather[0].main == "Rainy") {
                        icon.setBackgroundResource(R.drawable.ic_rain)
                    } else if (result.weather[0].main == "Windy") {
                        icon.setBackgroundResource(R.drawable.ic_icons8_winter_50)
                    } else if (result.weather[0].main == "Clouds") {
                        icon.setBackgroundResource(R.drawable.ic_clouds)
                    }
                    sunrise.text = "Wschód słońca: ${getTime(result.sys.sunrise.toLong() * 1000)}"
                    sunset.text = "Zachód słońca: ${getTime(result.sys.sunset.toLong() * 1000)}"
                }
                ,
                { error ->
                    Toast.makeText(
                        this,
                        "Wystąpił błąd. Sprawdź połączenie z internetem/poprawność danych",
                        Toast.LENGTH_LONG
                    ).show()
                } //error.message
            )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun beginSearchCity(searchString: String) {
        disposable = wikiApiServe.hitCity(
            searchString,
            "b9a31dcb2d2b84843ea2eba6b48ff5f9",
            "metric",
            "pl"
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    weather_city.text = "${result.name}"
                    weather_temp.text = "${result.main.temp.toInt()} °C"
                    tempDes.text = "Temperatura"
                    weather_pressure.text = "${result.main.pressure.toInt()} hPa"
                    pressureDes.text = "Ciśnienie"
                    weather_description.text = "${result.weather[0].description}"
//                    date.text = "${java.time.format.DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.ofEpochSecond(result.dt.toLong()))}"
                    weather_date.text = "${getDate(result.dt.toLong() * 1000, "dd MM yyyy")}"
                    if (result.weather[0].main == "Clear") {
                        weather_icon.setBackgroundResource(R.drawable.ic_wb_sunny_black_24dp)
                    } else if (result.weather[0].main == "Rainy") {
                        weather_icon.setBackgroundResource(R.drawable.ic_rain)
                    } else if (result.weather[0].main == "Windy") {
                        weather_icon.setBackgroundResource(R.drawable.ic_icons8_winter_50)
                    } else if (result.weather[0].main == "Clouds") {
                        weather_icon.setBackgroundResource(R.drawable.ic_clouds)
                    }
                    weather_sunrise.text = "Wschód słońca: ${getTime(result.sys.sunrise.toLong() * 1000)}"
                    weather_sunset.text = "Zachód słońca: ${getTime(result.sys.sunset.toLong() * 1000)}"


                }
                ,
                { error ->
                    Toast.makeText(
                        this,
                        "Wystąpił błąd. Sprawdź połączenie z internetem/poprawność danych",
                        Toast.LENGTH_LONG
                    ).show()
                } //error.message
            )

        val intent = Intent(this, WeatherDetails::class.java).apply {}
        this.startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }


    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        val formatter = SimpleDateFormat(dateFormat)
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds)
        return formatter.format(calendar.getTime())
    }

    fun getTime(milliSeconds: Long): String? {
        return String.format(
            " %d:%d",
            ((milliSeconds / (1000 * 60 * 60)) % 24),
            ((milliSeconds / (1000 * 60)) % 60)
        )
    }
}
