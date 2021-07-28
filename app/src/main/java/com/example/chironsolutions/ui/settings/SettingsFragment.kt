package com.example.chironsolutions.ui.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.chironsolutions.DatabaseHandler
import com.example.chironsolutions.MainActivity
import com.example.chironsolutions.R
import com.example.chironsolutions.databinding.FragmentSettingsBinding
import kotlinx.android.synthetic.main.fragment_settings.*
import java.lang.System.currentTimeMillis

lateinit var sharedPref: SharedPreferences

var RealDBP1: Double = 0.0
var RealDBP2: Double = 0.0
var RealDBP3: Double = 0.0
var RealDBP4: Double = 0.0
var RealDBP5: Double = 0.0

var RealSBP1: Double = 0.0
var RealSBP2: Double = 0.0
var RealSBP3: Double = 0.0
var RealSBP4: Double = 0.0
var RealSBP5: Double = 0.0

var CalibrateTime1: Long = 0L
var CalibrateTime2: Long = 0L
var CalibrateTime3: Long = 0L
var CalibrateTime4: Long = 0L
var CalibrateTime5: Long = 0L

var CalibrateTotal: Int = 0

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
                ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // val textView: TextView = binding.textNotifications
        settingsViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
        })
        return root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?) {

        sharedPref = (activity as MainActivity).getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        val btnClearUserData = idBtnClearUserData
        val btnCalibrate = idBtnCalibrate

        // set on-click listener
        btnClearUserData.setOnClickListener { view ->

            val fm: FragmentManager = requireActivity().getSupportFragmentManager()
            val clearDataDialogFragment = ClearDataDialogFragment()
            clearDataDialogFragment.show(fm, "clear_data_dialog")
        }

        btnCalibrate.setOnClickListener { view ->

            val fm: FragmentManager = requireActivity().getSupportFragmentManager()
            val calibrateDialogFragment = CalibrateDialogFragment()
            calibrateDialogFragment.show(fm, "fragment_calibrate_dialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CalibrateDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            var dialogView = inflater.inflate(R.layout.fragment_settings_calibrate_dialog, null)
            builder.setView(dialogView)

            val inputDBPSeekBar = dialogView.findViewById<SeekBar>(R.id.idDBPSeekBar)
            val inputSBPSeekBar = dialogView.findViewById<SeekBar>(R.id.idSBPSeekBar)
            val labelDBPValue = dialogView.findViewById<TextView>(R.id.idDBPValue)
            val labelSBPValue = dialogView.findViewById<TextView>(R.id.idSBPValue)
            val labelTitle = dialogView.findViewById<TextView>(R.id.idTitleLabel)
            labelTitle.setText(getString(R.string.calibration_title, CalibrateTotal+1))

            val DBPSeekBarListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    labelDBPValue.setText((seekBar.getProgress()+50).toString())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    labelDBPValue.setText((seekBar.getProgress()+50).toString())
                }

                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    labelDBPValue.setText((progress+50).toString())
                }
            }
            val SBPSeekBarListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    labelSBPValue.setText((seekBar.getProgress()+80).toString())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    labelSBPValue.setText((seekBar.getProgress()+80).toString())
                }

                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    labelSBPValue.setText((progress+80).toString())
                }
            }
            inputDBPSeekBar.setOnSeekBarChangeListener(DBPSeekBarListener)
            inputSBPSeekBar.setOnSeekBarChangeListener(SBPSeekBarListener)

            builder.setMessage("")
                .setPositiveButton("ok",
                    DialogInterface.OnClickListener { dialog, id ->

                        when (CalibrateTotal) {
                            0 -> {
                                CalibrateTime1 = currentTimeMillis()
                                CalibrateTotal = 1
                                RealDBP1 = (inputDBPSeekBar.getProgress()+50).toDouble()
                                RealSBP1 = (inputSBPSeekBar.getProgress()+80).toDouble()
                                val fm: FragmentManager = requireActivity().getSupportFragmentManager()
                                val calibrateDialogFragment = CalibrateDialogFragment()
                                calibrateDialogFragment.show(fm, "fragment_calibrate_dialog")
                            }
                            1 -> {
                                CalibrateTime2 = currentTimeMillis()
                                CalibrateTotal = 2
                                RealDBP2 = (inputDBPSeekBar.getProgress()+50).toDouble()
                                RealSBP2 = (inputSBPSeekBar.getProgress()+80).toDouble()
                                val fm: FragmentManager = requireActivity().getSupportFragmentManager()
                                val calibrateDialogFragment = CalibrateDialogFragment()
                                calibrateDialogFragment.show(fm, "fragment_calibrate_dialog")
                            }
                            2 -> {
                                CalibrateTime3 = currentTimeMillis()
                                CalibrateTotal = 3
                                RealDBP3 = (inputDBPSeekBar.getProgress()+50).toDouble()
                                RealSBP3 = (inputSBPSeekBar.getProgress()+80).toDouble()
                                val fm: FragmentManager = requireActivity().getSupportFragmentManager()
                                val calibrateDialogFragment = CalibrateDialogFragment()
                                calibrateDialogFragment.show(fm, "fragment_calibrate_dialog")
                            }
                            3 -> {
                                CalibrateTime4 = currentTimeMillis()
                                CalibrateTotal = 4
                                RealDBP4 = (inputDBPSeekBar.getProgress()+50).toDouble()
                                RealSBP4 = (inputSBPSeekBar.getProgress()+80).toDouble()
                                val fm: FragmentManager = requireActivity().getSupportFragmentManager()
                                val calibrateDialogFragment = CalibrateDialogFragment()
                                calibrateDialogFragment.show(fm, "fragment_calibrate_dialog")
                            }
                            4 -> {
                                CalibrateTime5 = currentTimeMillis()
                                CalibrateTotal = 0
                                RealDBP5 = (inputDBPSeekBar.getProgress()+50).toDouble()
                                RealSBP5 = (inputSBPSeekBar.getProgress()+80).toDouble()

                                var ecg = DoubleArray(5000)
                                var ppg = DoubleArray(5000)

                                var listOfData = (activity as MainActivity).readDataLast1000(CalibrateTime1)
                                for (i in listOfData.indices) {

                                    ecg[i] = listOfData[i].getECG()
                                    ppg[i] = listOfData[i].getPPG()
                                }
                                listOfData = (activity as MainActivity).readDataLast1000(CalibrateTime2)
                                for (i in listOfData.indices) {

                                    ecg[i+1000] = listOfData[i].getECG()
                                    ppg[i+1000] = listOfData[i].getPPG()
                                }
                                listOfData = (activity as MainActivity).readDataLast1000(CalibrateTime3)
                                for (i in listOfData.indices) {

                                    ecg[i+2000] = listOfData[i].getECG()
                                    ppg[i+2000] = listOfData[i].getPPG()
                                }
                                listOfData = (activity as MainActivity).readDataLast1000(CalibrateTime4)
                                for (i in listOfData.indices) {

                                    ecg[i+3000] = listOfData[i].getECG()
                                    ppg[i+3000] = listOfData[i].getPPG()
                                }
                                listOfData = (activity as MainActivity).readDataLast1000(CalibrateTime5)
                                for (i in listOfData.indices) {

                                    ecg[i+4000] = listOfData[i].getECG()
                                    ppg[i+4000] = listOfData[i].getPPG()
                                }

                                var realDBP = doubleArrayOf(
                                    RealDBP1, RealDBP2, RealDBP3, RealDBP4, RealDBP5)

                                var realSBP = doubleArrayOf(
                                    RealSBP1, RealSBP2, RealSBP3, RealSBP4, RealSBP5)


                                (activity as MainActivity).calibrate_wrapper(ecg, ppg, realDBP, realSBP)


                                 RealDBP1 = 0.0
                                 RealDBP2 = 0.0
                                 RealDBP3 = 0.0
                                 RealDBP4 = 0.0
                                 RealDBP5 = 0.0

                                 RealSBP1 = 0.0
                                 RealSBP2 = 0.0
                                 RealSBP3 = 0.0
                                 RealSBP4 = 0.0
                                 RealSBP5 = 0.0

                                 CalibrateTime1 = 0L
                                 CalibrateTime2 = 0L
                                 CalibrateTime3 = 0L
                                 CalibrateTime4 = 0L
                                 CalibrateTime5 = 0L

                                 CalibrateTotal = 0

                            }
                            else -> { // Note the block
                                println("Calibration error")
                            }
                        }



                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()




        } ?: throw IllegalStateException("Activity cannot be null")

    }

}

class ClearDataDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Are you sure you want to clear all User Data?")
                .setPositiveButton("ok",
                    DialogInterface.OnClickListener { dialog, id ->

                        var dataBaseHandler = DatabaseHandler(activity as MainActivity)
                        dataBaseHandler.deleteAll()
                        (activity as MainActivity).DataEntryComputed(-1, 0.0, 0.0, 0.0, 0.0, System.currentTimeMillis())

                        val editor = sharedPref.edit()
                        editor.putLong("SBP0", 0L)
                        editor.putLong("DBP0", 0L)
                        editor.putLong("PTT0", 0L)
                        editor.putInt("isCalibrated", 0)
                        editor.apply()

                        RealDBP1 = 0.0
                        RealDBP2 = 0.0
                        RealDBP3 = 0.0
                        RealDBP4 = 0.0
                        RealDBP5 = 0.0

                        RealSBP1 = 0.0
                        RealSBP2 = 0.0
                        RealSBP3 = 0.0
                        RealSBP4 = 0.0
                        RealSBP5 = 0.0

                        CalibrateTime1 = 0L
                        CalibrateTime2 = 0L
                        CalibrateTime3 = 0L
                        CalibrateTime4 = 0L
                        CalibrateTime5 = 0L

                        CalibrateTotal = 0
                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}