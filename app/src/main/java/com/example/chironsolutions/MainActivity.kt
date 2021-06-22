package com.example.chironsolutions

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chironsolutions.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_settings))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //testDB
        DataEntry(-1, 5.1, 4.0, 70.0, 90.0, 500)
        DataEntry(-1, 5.2, 7.0, 34.0, 99.0, 600)
    }

    fun DataEntry(id: Int, PPG: Double, ECG: Double, DBP: Double, SBP: Double,  date: Long) {

        var userData: UserDataModel
        try {
            userData = UserDataModel(id, PPG, ECG, DBP, SBP, date)
        }
        catch(e: Exception){
            userData = UserDataModel(-1, 0.0,0.0,0.0,0.0,0)
        }

        var dataBaseHandler = DatabaseHandler(this@MainActivity)
        dataBaseHandler.addOne(userData)
    }



    fun sum(a: Int, b: Int): Int {
        return a + b
    }
}