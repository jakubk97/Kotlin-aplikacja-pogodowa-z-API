package com.example.kuba

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    private val wikiApiServe by lazy {
        WeatherApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_search.setOnClickListener {
            if (edit_search.text.toString().isNotEmpty()) {
                beginSearch(edit_search.text.toString())
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(btn_search.windowToken, 0)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun beginSearch(searchString: String) {
        disposable = wikiApiServe.hitCountCheck(
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
                { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }
            )
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
