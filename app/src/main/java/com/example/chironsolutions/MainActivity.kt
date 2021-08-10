package com.example.chironsolutions

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.CALLBACK_TYPE_FIRST_MATCH
import android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.system.Os.close
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
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
    var firstLoop = 1

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner


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
        val editor = sharedPref.edit()
        editor.putInt("isBluetoothOn", 0)
        editor.putInt("calibrationStep", 1)
        editor.apply()


        if (ContextCompat.checkSelfPermission(baseContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_CODE)
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

            //val filter = ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB"))).build()


            scanLeDevice()
        }

        /*
        GlobalScope.launch(Dispatchers.IO) {

            //debug

            editor = sharedPref.edit()
            editor.putInt("isBluetoothOn", 1)
            editor.apply()


            val editor = sharedPref.edit()
            editor.putLong("SBP0", 0L)
            editor.putLong("DBP0", 0L)
            editor.putLong("PTT0", 0L)
            editor.putInt("isCalibrated", 0)
            editor.apply()

            var assetManager = getAssets();
            var myInput = assetManager.open("testdataFull.xls");
            myFileSystem = POIFSFileSystem(myInput)

            var dataBaseHandler = DatabaseHandler(this@MainActivity)
            dataBaseHandler.deleteAll()
            (this@MainActivity).DataEntryComputed(-1, 0.0, 0.0, 0.0, 0.0, System.currentTimeMillis())


            inputData()
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

        GlobalScope.launch(Dispatchers.IO) {

            Thread.sleep(10000)
            val mainHandler = Handler(Looper.getMainLooper())

            mainHandler.post(object : Runnable {
                override fun run() {

                    if(sharedPref.getInt("isBluetoothOn", 0) == 1) {

                        if(firstLoop == 1){
                            Thread.sleep(10000)
                            firstLoop = 0
                        }

                        val listOfData = this@MainActivity.readDataLast1000(System.currentTimeMillis())
                        //var listOfData = this@MainActivity.readDebugDataLast1000(1000)

                        var ecg = DoubleArray(1000)
                        var ppg = DoubleArray(1000)
                        for (i in listOfData.indices) {

                            ecg[i] = listOfData[i].getECG()
                            ppg[i] = listOfData[i].getPPG()
                        }

                        val ptt = calculate_PTT_wrapper(ecg, ppg)

                        if (sharedPref.getInt("isCalibrated", 0) == 1) {


                            calculate_DBP_wrapper(ptt)
                            sendBroadcast(Intent("new_data"))
                        }
                        else if (sharedPref.getInt("isCalibrated", 0) == 0) {

                            when (sharedPref.getInt("calibrationStep", 1)) {
                                1 -> {
                                    editor.putLong("PTT1", java.lang.Double.doubleToRawLongBits(ptt))
                                    editor.putInt("calibrationStep", 2)
                                    editor.apply()
                                }
                                2 -> {
                                    editor.putLong("PTT2", java.lang.Double.doubleToRawLongBits(ptt))
                                    editor.putInt("calibrationStep", 3)
                                    editor.apply()
                                }
                                3 -> {
                                    editor.putLong("PTT3", java.lang.Double.doubleToRawLongBits(ptt))
                                    editor.putInt("calibrationStep", 4)
                                    editor.apply()
                                }
                                4 -> {
                                    editor.putLong("PTT4", java.lang.Double.doubleToRawLongBits(ptt))
                                    editor.putInt("calibrationStep", 5)
                                    editor.apply()
                                }
                                5 -> {
                                    editor.putLong("PTT5", java.lang.Double.doubleToRawLongBits(ptt))
                                    editor.putInt("calibrationStep", 0)
                                    editor.apply()
                                }
                                else -> { // Note the block

                                }
                            }


                        }
                    }
                    mainHandler.postDelayed(this, 4000)
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

    fun scanLeDevice() {

        val builder = ScanFilter.Builder()
        builder.setDeviceName("Harp")
        val scanFilter = builder.build()
        val filters = listOf(scanFilter)

        val settings = ScanSettings.Builder()
        settings.setCallbackType(CALLBACK_TYPE_FIRST_MATCH)
        settings.setScanMode(SCAN_MODE_LOW_LATENCY)

//        if (!scanning) { // Stops scanning after a pre-defined scan period.
//            handler.postDelayed({
//                scanning = false
//                bluetoothLeScanner?.stopScan(leScanCallback)
//            }, SCAN_PERIOD)
//            scanning = true
            bluetoothLeScanner?.startScan(filters, settings.build(), leScanCallback)
            Toast.makeText(this, R.string.starting_scan, Toast.LENGTH_SHORT).show();
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

//            Toast.makeText(this@MainActivity, getString(R.string.device_found, result.device.name), Toast.LENGTH_SHORT).show();

            //val device = result.device //can maybe skip name check
//            if( device.name == "NAMEHarp"){

                //var bluetoothGatt: BluetoothGatt? = null
            bluetoothLeScanner?.stopScan(leScanCallbackCancel)
            Thread.sleep(2000)
            result.device.connectGatt(this@MainActivity, true, gattCallback)
//            }

//            var intent = Intent("new_ble")
//            sendBroadcast(Intent(intent))
        }
    }

    private val leScanCallbackCancel: ScanCallback = object : ScanCallback() {

    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {

            Thread.sleep(1000)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    var bool = gatt?.requestMtu(256)
                    //Toast.makeText(this@MainActivity, R.string.device_connected, Toast.LENGTH_SHORT).show();
                    gatt?.discoverServices()
                }
                else {
//                    Toast.makeText(this@MainActivity, R.string.connection_closed, Toast.LENGTH_SHORT).show();
                    gatt?.close()
                }
            }
            else{
//                Toast.makeText(this@MainActivity, getString(R.string.connection_error, status), Toast.LENGTH_SHORT).show()
                gatt?.close()
            }
        }


        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {

            //Toast.makeText(this@MainActivity, R.string.services_discovered, Toast.LENGTH_SHORT).show();

            Thread.sleep(1000)
            val characteristic = gatt?.getService(UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB"))
                ?.getCharacteristic(UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB"))
            gatt?.setCharacteristicNotification(characteristic, true)

            val editor = sharedPref.edit()
            editor.putInt("isBluetoothOn", 1)
            editor.apply()
            sendBroadcast(Intent("bluetooth_on"))
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            characteristic?.let{

                var dataInput = String(characteristic.value)
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

    fun readDataLast5Min() : List<UserDataModel>{

        var dataBaseHandler = DatabaseHandler(this@MainActivity)
        var allData = dataBaseHandler.getLast5Min();
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
        external fun calculate_PTT(ECG: DoubleArray, PPG: DoubleArray): Double

        external fun calculate_DBP(SBP0: Double, DBP0: Double, PTT0: Double, PTTcurrent: Double, gamma: Double): Double

        external fun calibrate(
            PTT: DoubleArray, RealDBP: DoubleArray, RealSBP: DoubleArray): DoubleArray // SBP0, DBP0, PTT0, fPTT0, fDBP0

        init {
            System.loadLibrary("native-lib")
        }
    }

    fun calculate_PTT_wrapper(ECG: DoubleArray, PPG: DoubleArray): Double{


        return calculations.calculate_PTT(ECG, PPG)
    }

    fun calibrate_wrapper(PTT: DoubleArray, RealDBP: DoubleArray, RealSBP: DoubleArray){


        var value = calculations.calibrate(PTT, RealDBP, RealSBP) // SBP0, DBP0, PTT0, fPTT0, fDBP0

        val editor = sharedPref.edit()
        editor.putLong("SBP0", java.lang.Double.doubleToRawLongBits(value[0]))
        editor.putLong("DBP0", java.lang.Double.doubleToRawLongBits(value[1]))
        editor.putLong("PTT0", java.lang.Double.doubleToRawLongBits(value[2]))
        editor.putInt("isCalibrated", 1)
        editor.putInt("calibrationStep", 1)
        editor.apply()

    }

    fun calculate_DBP_wrapper(PTT: Double){

//debug off
        //var latestDBP = calculations.calculate_DBP(114.8, 66.4, 0.1, ECG, PPG, gamma)
        var latestDBP = calculations.calculate_DBP(
            java.lang.Double.longBitsToDouble(sharedPref.getLong("SBP0", 0L)),
            java.lang.Double.longBitsToDouble(sharedPref.getLong("DBP0", 0L)),
            java.lang.Double.longBitsToDouble(sharedPref.getLong("PTT0", 0L)),
            PTT, gamma)

        if (latestDBP < 30 || latestDBP > 200){
            latestDBP = 0.0
            Toast.makeText(this, R.string.reposition_module, Toast.LENGTH_SHORT).show();
        }

        DataEntryComputed(-1, 0.0, 0.0, latestDBP, 0.0, System.currentTimeMillis())

    }

    fun inputData() {

        var xlWb = WorkbookFactory.create(myFileSystem)
        val xlWs = xlWb.getSheetAt(0)

        row = 1
        column = 0
        var ecg: Double
        var ppg: Double


        while (row < 6000){

            ecg = (xlWs.getRow(row).getCell(column)).getNumericCellValue()
            ppg = (xlWs.getRow(row).getCell(column+1)).getNumericCellValue()

            DataEntryRaw(-1, ppg, ecg, 0.0 /*((row.toDouble()) % 10) + 80*/, 0.0, System.currentTimeMillis())
            row++;
        }





//        while(column <= 10) {
//
//            ecg = (xlWs.getRow(row).getCell(column)).getNumericCellValue()
//            ppg = (xlWs.getRow(row).getCell(column+ 11)).getNumericCellValue()
////            var ecg = readFromExcelFile(myFileSystem, column, row) //ecg
////            var ppg = readFromExcelFile(myFileSystem, column + 11, row) //ppg
//
//            //var ecg = 0.0
//            //var ppg = 0.0
//
//            DataEntryRaw(-1, ppg, ecg, 0.0 /*((row.toDouble()) % 10) + 80*/, 0.0, System.currentTimeMillis())
//
//            if (row >= 999) {
//
//                row = 0
//                column++
//
//                if (column == 10) {
//
//                    break
//                }
//            } else {
//
//                row++
//                //println(row)
//            }
//        }
    }


    override fun onDestroy() {
        super.onDestroy()

    }


}