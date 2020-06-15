package com.example.kuba.ui


import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi

import com.example.kuba.R
import com.example.kuba.WeatherApiService
import com.example.kuba.helpers.DateAndTimeHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.search_city.*
import kotlinx.android.synthetic.main.weather_data.*

class CityFragment : Fragment() {

    private var disposable: Disposable? = null

    private val wikiApiServe by lazy {
        WeatherApiService.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_city, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_search_city.setOnClickListener {
            if (edit_search_city.text.toString().isNotEmpty()) {
                beginSearchCity(edit_search_city.text.toString())
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(button_search_city.windowToken, 0)
            }
        }
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
                    weather_city.text = result.name
                    weather_temp.text = "${result.main.temp} °C"
                    weather_pressure.text = "${result.main.pressure} hPa"
                    weather_humidity.text = "${result.main.humidity} %"
                    weather_description.text = result.weather[0].description
                    weather_date.text =
                        "${DateAndTimeHelper().getDate(result.dt.toLong() * 1000, "dd MM yyyy")}"
                    if (result.weather[0].main == "Clear") {
                        weather_icon.setBackgroundResource(R.drawable.ic_wb_sunny_black_24dp)
                    } else if (result.weather[0].main == "Rainy") {
                        weather_icon.setBackgroundResource(R.drawable.ic_rain)
                    } else if (result.weather[0].main == "Windy") {
                        weather_icon.setBackgroundResource(R.drawable.ic_icons8_winter_50)
                    } else if (result.weather[0].main == "Clouds") {
                        weather_icon.setBackgroundResource(R.drawable.ic_clouds)
                    }
                    weather_icon_pressure.setBackgroundResource(R.drawable.ic_pressure)
                    weather_icon_humidity.setBackgroundResource(R.drawable.ic_humidity)
                    sunrise_label.setText("Wschód")
                    sunset_label.setText("Zachód")
                    weather_sunrise.text =
                        "${DateAndTimeHelper().getTime(result.sys.sunrise.toLong() * 1000)}"
                    weather_sunset.text =
                        "${DateAndTimeHelper().getTime(result.sys.sunset.toLong() * 1000)}"
                },
                {
                    Toast.makeText(
                        requireContext(),
                        "Wystąpił błąd. Sprawdź połączenie z internetem lub poprawność wpisanego miasta",
                        Toast.LENGTH_LONG
                    ).show()
                } //error.message
            )
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}
