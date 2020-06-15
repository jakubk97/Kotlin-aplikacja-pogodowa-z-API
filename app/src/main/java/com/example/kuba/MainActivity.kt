package com.example.kuba

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kuba.ui.CityFragment
import com.example.kuba.ui.LocalizationFragment
import com.example.kuba.ui.ZipFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.weather_data.*
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

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CityFragment())
            .commit()
        bottomNav.setOnNavigationItemSelectedListener { item ->
            val fragment: Fragment

            when (item.itemId) {
                R.id.menu_city -> {
                    fragment =
                        CityFragment()
                }
                R.id.menu_localization -> {
                    fragment =
                        LocalizationFragment()
                }
                R.id.menu_zip -> {
                    fragment =
                        ZipFragment()
                }
                else -> {
                    fragment = CityFragment()
                }
            }

            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit()
            true
        }
    }




    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }



}
