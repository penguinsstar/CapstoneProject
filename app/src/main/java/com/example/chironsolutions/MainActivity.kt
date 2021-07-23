package com.example.chironsolutions

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences.Editor
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


    var column: Int = 0
    var row: Int = 0
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



        GlobalScope.launch(Dispatchers.IO) {

            //debug set to true
            if (true) {

                var assetManager = getAssets();
                var myInput = assetManager.open("testdata.xls");
                myFileSystem = POIFSFileSystem(myInput)

                var dataBaseHandler = DatabaseHandler(this@MainActivity)
                dataBaseHandler.deleteAll()
                (this@MainActivity).DataEntry(-1, 0.0, 0.0, 0.0, 0.0, 0)

                inputData()

            }
        }
        GlobalScope.launch(Dispatchers.Main) {

            val mainHandler = Handler(Looper.getMainLooper())

            mainHandler.post(object : Runnable {
                override fun run() {

                    sendBroadcast(Intent("new_data"))
                    mainHandler.postDelayed(this, 1000)
                }
            })
        }

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

    fun readFromExcelFile(inputStream: POIFSFileSystem, columnNumber: Int, rowNumber: Int ) : Double {

        //val inputStream = FileInputStream(filepath)
        //Instantiate Excel workbook using existing file:
        var xlWb = WorkbookFactory.create(inputStream)

        //Cell index specifies the column within the chosen row (starting at 0):
        //Row index specifies the row in the worksheet (starting at 0):

        //Get reference to first sheet:
        val xlWs = xlWb.getSheetAt(0)
        return (xlWs.getRow(rowNumber).getCell(columnNumber)).getNumericCellValue()
    }


    fun inputData() {

        while(column <= 10) {

            var ecg = readFromExcelFile(myFileSystem, column, row) //ecg
            var ppg = readFromExcelFile(myFileSystem, column + 11, row) //ppg

            //var ecg = 0.0
            //var ppg = 0.0

            DataEntry(-1, ppg, ecg, ((row.toDouble()) % 10) + 80, 0.0, System.currentTimeMillis())

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

    fun calibrate(){

        var value: Double = 2.0

        val sharedPref = this@MainActivity.getPreferences(Context.MODE_PRIVATE)


        val editor = sharedPref.edit()
        editor.putLong("stringname", java.lang.Double.doubleToRawLongBits(value))
        editor.apply()




        var output = java.lang.Double.longBitsToDouble(sharedPref.getLong("stringname", 0L))
    }


    override fun onDestroy() {
        super.onDestroy()


    }


}