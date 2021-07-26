package com.example.chironsolutions

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chironsolutions.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var sharedPref: SharedPreferences
    var value_SBP0: Double = 0.0
    var value_DBP0: Double = 0.0
    var value_PTT0: Double = 0.0
    var column: Int = 0
    var row: Int = 0
    var gamma = 0.031


    lateinit var myFileSystem :  POIFSFileSystem

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



        sharedPref = this@MainActivity.getPreferences(Context.MODE_PRIVATE)
        value_SBP0 = java.lang.Double.longBitsToDouble(sharedPref.getLong("SBP0", 0L))
        value_DBP0 = java.lang.Double.longBitsToDouble(sharedPref.getLong("DBP0", 0L))
        value_PTT0 = java.lang.Double.longBitsToDouble(sharedPref.getLong("PTT0", 0L))

        GlobalScope.launch(Dispatchers.IO) {

            //debug set to true
            if (true) {

                var assetManager = getAssets();
                var myInput = assetManager.open("testdata.xls");
                myFileSystem = POIFSFileSystem(myInput)

                var dataBaseHandler = DatabaseHandler(this@MainActivity)
                dataBaseHandler.deleteAll()
                (this@MainActivity).DataEntryRaw(-1, 0.0, 0.0, 0.0, 0.0, System.currentTimeMillis())
                (this@MainActivity).DataEntryComputed(-1, 0.0, 0.0, 0.0, 0.0, System.currentTimeMillis())

                inputData()

                var listOfData = this@MainActivity.readDataLast1000(System.currentTimeMillis())
                var ecg = DoubleArray(1000)
                var ppg = DoubleArray(1000)
                for (i in listOfData.indices) {

                    ecg[i] = listOfData[i].getECG()
                    ppg[i] = listOfData[i].getPPG()
                }

                calculate_DBP_wrapper(ecg, ppg)


            }

        }
        GlobalScope.launch(Dispatchers.Main) {

            val mainHandler = Handler(Looper.getMainLooper())

            mainHandler.post(object : Runnable {
                override fun run() {

                    sendBroadcast(Intent("new_data"))
                    mainHandler.postDelayed(this, 10000)
                }
            })
        }

    }





    fun DataEntryRaw(id: Int, PPG: Double, ECG: Double, DBP: Double, SBP: Double, date: Long) {

        var userData: UserDataModel
        try {
            userData = UserDataModel(id, PPG, ECG, DBP, SBP, date)
        }
        catch(e: Exception){
            userData = UserDataModel(-1, 0.0,0.0,0.0,0.0,0)
        }

        var dataBaseHandler = DatabaseHandler(this@MainActivity)
        dataBaseHandler.addOneRaw(userData)


        //sendBroadcast(Intent("new_data"))
    }

    fun DataEntryComputed(id: Int, PPG: Double, ECG: Double, DBP: Double, SBP: Double, date: Long) {

        var userData: UserDataModel
        try {
            userData = UserDataModel(id, PPG, ECG, DBP, SBP, date)
        }
        catch(e: Exception){
            userData = UserDataModel(-1, 0.0,0.0,0.0,0.0,0)
        }

        var dataBaseHandler = DatabaseHandler(this@MainActivity)
        dataBaseHandler.addOneComputed(userData)


        //sendBroadcast(Intent("new_data"))
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

    fun readDataLast1000(time: Long) : List<UserDataModel>{

        var dataBaseHandler = DatabaseHandler(this@MainActivity)
        var allData = dataBaseHandler.getLast1000(time);
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
            gamma: Double, CalibrationMode: Boolean,
        ): DoubleArray // SBP0, DBP0, PTT0, fPTT0, fDBP0

        init {
            System.loadLibrary("native-lib")
        }
    }

    fun inputData() {

        var xlWb = WorkbookFactory.create(myFileSystem)
        val xlWs = xlWb.getSheetAt(0)

        row = 0
        column = 0
        var ecg: Double
        var ppg: Double

        while(column <= 6) {

            ecg = (xlWs.getRow(row).getCell(column)).getNumericCellValue()
            ppg = (xlWs.getRow(row).getCell(column+ 11)).getNumericCellValue()
//            var ecg = readFromExcelFile(myFileSystem, column, row) //ecg
//            var ppg = readFromExcelFile(myFileSystem, column + 11, row) //ppg

            //var ecg = 0.0
            //var ppg = 0.0

            DataEntryRaw(-1, ppg, ecg, 0.0 /*((row.toDouble()) % 10) + 80*/, 0.0, System.currentTimeMillis())

            if (row >= 999) {

                row = 0
                column++

                if (column == 10) {

                    break
                }
            } else {

                row++
                println(row)
            }
        }
    }


    fun calibrate_wrapper(ECG: DoubleArray, PPG: DoubleArray, RealDBP: DoubleArray, RealSBP: DoubleArray){


        var value = calculations.calibrate(ECG, PPG, RealDBP, RealSBP, gamma, false) // SBP0, DBP0, PTT0, fPTT0, fDBP0

        value_SBP0 = value[0]
        value_DBP0 = value[1]
        value_PTT0 = value[2]

        val editor = sharedPref.edit()
        editor.putLong("SBP0", java.lang.Double.doubleToRawLongBits(value[0]))
        editor.putLong("DBP0", java.lang.Double.doubleToRawLongBits(value[1]))
        editor.putLong("PTT0", java.lang.Double.doubleToRawLongBits(value[2]))
        editor.apply()


    }

    fun calculate_DBP_wrapper(ECG: DoubleArray, PPG: DoubleArray){


        var latestDBP = calculations.calculate_DBP(value_SBP0, value_DBP0, value_PTT0, 0.0, 0.0, ECG, PPG, gamma, false)
        DataEntryComputed(-1, 0.0, 0.0, latestDBP, 0.0, System.currentTimeMillis())

    }


    override fun onDestroy() {
        super.onDestroy()


    }


}