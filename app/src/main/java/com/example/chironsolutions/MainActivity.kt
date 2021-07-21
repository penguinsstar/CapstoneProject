package com.example.chironsolutions

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chironsolutions.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
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
        DataEntry(-1, 5.1, 4.0, 70.0, 90.0, System.currentTimeMillis())
        DataEntry(-1, 5.2, 7.0, 34.0, 99.0, System.currentTimeMillis())
        DataEntry(-1, 5.2, 7.0, 35.0, 98.0, System.currentTimeMillis())

        Handler().postDelayed(Runnable {
            DataEntry(-1, 5.2, 7.0, 100.0, 98.0, System.currentTimeMillis())
        }, 5000)
        Handler().postDelayed(Runnable {
            DataEntry(-1, 5.2, 7.0, 80.0, 98.0, System.currentTimeMillis())
        }, 10000)


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


        sendBroadcast(Intent("new_data"))
    }

    fun readDataAll(): List<UserDataModel>{

        var dataBaseHandler = DatabaseHandler(this@MainActivity)
        var allData = dataBaseHandler.getAll()
        return allData
    }

    fun readDataLast24Hours() : List<UserDataModel>{

        var dataBaseHandler = DatabaseHandler(this@MainActivity)
        var allData = dataBaseHandler.getLast24Hours();
        return allData
    }

    fun readDataLatest(): UserDataModel {

        var dataBaseHandler = DatabaseHandler(this@MainActivity)
        var allData = dataBaseHandler.getLatest()
        return allData
    }

    object calculations {
        external fun calculate_DBP(SBP0: Double, DBP0: Double, PTT0: Double, fPTT0: Double, fDBP0: Double,
                                   ECG: DoubleArray, PPG: DoubleArray, gamma: Double, CalibrationMode: Boolean): Double
        external fun calibrate(
            ECG: DoubleArray, PPG: DoubleArray, RealDBP: DoubleArray, RealSBP: DoubleArray,
            gamma: Boolean, CalibrationMode: Boolean,
        ): DoubleArray // SBP0, DBP0, PTT0, fPTT0, fDBP0

        init {
            System.loadLibrary("native-lib")
        }
    }


    fun sum(a: Int, b: Int): Int {
        return a + b
    }
}