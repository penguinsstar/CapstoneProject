package com.example.chironsolutions

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chironsolutions.databinding.ActivityMainBinding
import com.example.chironsolutions.ui.settings.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var sharedPref: SharedPreferences
    lateinit var myFileSystem :  POIFSFileSystem
    var column: Int = 0
    var row: Int = 0
    var gamma = 0.031
    val REQUEST_ENABLE_BT = 0
    val PERMISSION_CODE = 1

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    private var scanning = false
    private val handler = Handler()
    private val SCAN_PERIOD: Long = 15000


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



        sharedPref = this@MainActivity.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(baseContext,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    PERMISSION_CODE)
            }
        }
        if (bluetoothAdapter != null) {

            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
                finish();
            }
            if (bluetoothAdapter.isEnabled == false) {

                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }

            val filter = ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB"))).build()
            val filters = listOf(filter)
            val settings = ScanSettings.Builder().build()
            scanLeDevice(filters, settings)
        }

        /*
        GlobalScope.launch(Dispatchers.IO) {

            //debug


            val editor = sharedPref.edit()
            editor.putLong("SBP0", 0L)
            editor.putLong("DBP0", 0L)
            editor.putLong("PTT0", 0L)
            editor.putInt("isCalibrated", 0)
            editor.apply()

            var assetManager = getAssets();
            var myInput = assetManager.open("testdata.xls");
            myFileSystem = POIFSFileSystem(myInput)

            var dataBaseHandler = DatabaseHandler(this@MainActivity)
            dataBaseHandler.deleteAll()
            (this@MainActivity).DataEntryComputed(-1, 0.0, 0.0, 0.0, 0.0, System.currentTimeMillis())


            inputData()
            inputData()


        }

        GlobalScope.launch(Dispatchers.Default) {

            //debug set to true
            if (true) {

                Thread.sleep(30000)

                var ecg = DoubleArray(5000)
                var ppg = DoubleArray(5000)

                var listOfData = readDebugDataLast1000(1000)
                for (i in listOfData.indices) {

                    ecg[i] = listOfData[i].getECG()
                    ppg[i] = listOfData[i].getPPG()
                }
                listOfData = readDebugDataLast1000(2000)
                for (i in listOfData.indices) {

                    ecg[i+1000] = listOfData[i].getECG()
                    ppg[i+1000] = listOfData[i].getPPG()
                }
                listOfData = readDebugDataLast1000(3000)
                for (i in listOfData.indices) {

                    ecg[i+2000] = listOfData[i].getECG()
                    ppg[i+2000] = listOfData[i].getPPG()
                }
                listOfData = readDebugDataLast1000(4000)
                for (i in listOfData.indices) {

                    ecg[i+3000] = listOfData[i].getECG()
                    ppg[i+3000] = listOfData[i].getPPG()
                }
                listOfData = readDebugDataLast1000(5000)
                for (i in listOfData.indices) {

                    ecg[i+4000] = listOfData[i].getECG()
                    ppg[i+4000] = listOfData[i].getPPG()
                }

                var realDBP = doubleArrayOf(
                    64.0, 66.0,  66.0, 67.0, 68.0)

                var realSBP = doubleArrayOf(
                    94.0, 96.0, 96.0, 97.0, 98.0)

                calibrate_wrapper(ecg, ppg, realDBP, realSBP)

            }

        }
        */

        GlobalScope.launch(Dispatchers.Main) {

            Thread.sleep(10000)
            val mainHandler = Handler(Looper.getMainLooper())

            mainHandler.post(object : Runnable {
                override fun run() {

                    if(sharedPref.getInt("isCalibrated", 0) == 1) {

                        var listOfData = this@MainActivity.readDataLast1000(System.currentTimeMillis())
                        //var listOfData = this@MainActivity.readDebugDataLast1000(1000)
                        var ecg = DoubleArray(1000)
                        var ppg = DoubleArray(1000)
                        for (i in listOfData.indices) {

                            ecg[i] = listOfData[i].getECG()
                            ppg[i] = listOfData[i].getPPG()
                        }

                        calculate_DBP_wrapper(ecg, ppg)
                        sendBroadcast(Intent("new_data"))
                    }
                    mainHandler.postDelayed(this, 10000)
                }
            })
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_ENABLE_BT -> if (resultCode == RESULT_OK) {

            } else {
                // User did not enable Bluetooth or an error occurred
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun scanLeDevice( filters: List<ScanFilter>, settings: ScanSettings) {
//        if (!scanning) { // Stops scanning after a pre-defined scan period.
//            handler.postDelayed({
//                scanning = false
//                bluetoothLeScanner?.stopScan(leScanCallback)
//            }, SCAN_PERIOD)
//            scanning = true
            bluetoothLeScanner?.startScan(filters, settings, leScanCallback)
//        } else {
//            scanning = false
//            bluetoothLeScanner?.stopScan(leScanCallback)
//        }
    }

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            //leDeviceListAdapter.addDevice(result.device)
            //leDeviceListAdapter.notifyDataSetChanged()

            var device = result.device //can maybe skip name check
//            if( device.name == "NAMEHarp"){

                //var bluetoothGatt: BluetoothGatt? = null

                device.connectGatt(this@MainActivity, true, gattCallback)
//            }

//            var intent = Intent("new_ble")
//            sendBroadcast(Intent(intent))
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if(newState == BluetoothGatt.STATE_CONNECTED) {
                //gatt?.requestMtu(256)
                gatt?.discoverServices()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {

            val characteristic = gatt?.getService(UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB"))
                ?.getCharacteristic(UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB"))
            gatt?.setCharacteristicNotification(characteristic, true)

        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            characteristic?.let{

                var dataInput = characteristic.value.toString()
                var string = dataInput.split(" ")

                DataEntryRaw(-1, string[1].toDouble(), string[0].toDouble(), 0.0, 0.0, System.currentTimeMillis())
            }
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

    fun readDebugDataLast1000(id: Int) : List<UserDataModel>{

        var dataBaseHandler = DatabaseHandler(this@MainActivity)
        var allData = dataBaseHandler.getDebugLast1000(id);
        return allData
    }

    fun readDataLatest(): UserDataModel {

        var dataBaseHandler = DatabaseHandler(this@MainActivity)
        var allData = dataBaseHandler.getLatest()
        return allData
    }

    object calculations {
        external fun calculate_DBP(SBP0: Double, DBP0: Double, PTT0: Double,
                                   ECG: DoubleArray, PPG: DoubleArray, gamma: Double): Double
        external fun calibrate(
            ECG: DoubleArray, PPG: DoubleArray, RealDBP: DoubleArray, RealSBP: DoubleArray): DoubleArray // SBP0, DBP0, PTT0, fPTT0, fDBP0

        init {
            System.loadLibrary("native-lib")
        }
    }

    fun calibrate_wrapper(ECG: DoubleArray, PPG: DoubleArray, RealDBP: DoubleArray, RealSBP: DoubleArray){


        var value = calculations.calibrate(ECG, PPG, RealDBP, RealSBP) // SBP0, DBP0, PTT0, fPTT0, fDBP0

        val editor = sharedPref.edit()
        editor.putLong("SBP0", java.lang.Double.doubleToRawLongBits(value[0]))
        editor.putLong("DBP0", java.lang.Double.doubleToRawLongBits(value[1]))
        editor.putLong("PTT0", java.lang.Double.doubleToRawLongBits(value[2]))
        editor.putInt("isCalibrated", 1)
        editor.apply()


    }

    fun calculate_DBP_wrapper(ECG: DoubleArray, PPG: DoubleArray){

//debug off
        //var latestDBP = calculations.calculate_DBP(114.8, 66.4, 0.1, ECG, PPG, gamma)
        var latestDBP = calculations.calculate_DBP(java.lang.Double.longBitsToDouble(sharedPref.getLong("SBP0", 0L)),
            java.lang.Double.longBitsToDouble(sharedPref.getLong("DBP0", 0L)), java.lang.Double.longBitsToDouble(sharedPref.getLong("PTT0", 0L)), ECG, PPG, gamma)
        DataEntryComputed(-1, 0.0, 0.0, latestDBP, 0.0, System.currentTimeMillis())

    }

    fun inputData() {

        var xlWb = WorkbookFactory.create(myFileSystem)
        val xlWs = xlWb.getSheetAt(0)

        row = 0
        column = 0
        var ecg: Double
        var ppg: Double

        while(column <= 10) {

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
                //println(row)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

    }


}