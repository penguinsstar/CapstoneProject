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

        sharedPref = (activity as MainActivity).getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong("CalibrateTime1", 0L)
        editor.putLong("CalibrateTime2", 0L)
        editor.putLong("CalibrateTime3", 0L)
        editor.putLong("CalibrateTime4", 0L)
        editor.putInt("RealDBP1", 0)
        editor.putInt("RealSBP1", 0)
        editor.putInt("RealDBP2", 0)
        editor.putInt("RealSBP2", 0)
        editor.putInt("RealDBP3", 0)
        editor.putInt("RealSBP3", 0)
        editor.putInt("RealDBP4", 0)
        editor.putInt("RealSBP4", 0)
        editor.putInt("CalibrateTotal", 0)
        editor.apply()

        val btnClearUserData = idBtnClearUserData
        val btnCalibrate = idBtnCalibrate

        // set on-click listener
        btnClearUserData.setOnClickListener { view ->

            var dataBaseHandler = DatabaseHandler(activity as MainActivity)
            dataBaseHandler.deleteAll()
            (activity as MainActivity).DataEntryRaw(-1, 0.0, 0.0, 0.0, 0.0, 0)
            (activity as MainActivity).DataEntryComputed(-1, 0.0, 0.0, 0.0, 0.0, 0)
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

            builder.setMessage("Calibrating")
                .setPositiveButton("ok",
                    DialogInterface.OnClickListener { dialog, id ->

                        val editor = sharedPref.edit()
                        when (sharedPref.getInt("CalibrateTotal", 0)) {
                            0 -> {
                                editor.putLong("CalibrateTime1", currentTimeMillis())
                                editor.putInt("CalibrateTotal", 1)
                                editor.putInt("RealDBP1", inputDBPSeekBar.getProgress())
                                editor.putInt("RealSBP1", inputSBPSeekBar.getProgress())
                                editor.apply()
                            }
                            1 -> {
                                editor.putLong("CalibrateTime2", currentTimeMillis())
                                editor.putInt("CalibrateTotal", 2)
                                editor.putInt("RealDBP2", inputDBPSeekBar.getProgress())
                                editor.putInt("RealSBP2", inputSBPSeekBar.getProgress())
                                editor.apply()
                            }
                            2 -> {
                                editor.putLong("CalibrateTime3", currentTimeMillis())
                                editor.putInt("CalibrateTotal", 3)
                                editor.putInt("RealDBP3", inputDBPSeekBar.getProgress())
                                editor.putInt("RealSBP3", inputSBPSeekBar.getProgress())
                                editor.apply()
                            }
                            3 -> {
                                editor.putLong("CalibrateTime4", currentTimeMillis())
                                editor.putInt("CalibrateTotal", 4)
                                editor.putInt("RealDBP4", inputDBPSeekBar.getProgress())
                                editor.putInt("RealSBP4", inputSBPSeekBar.getProgress())
                                editor.apply()
                            }
                            4 -> {

                                var time = currentTimeMillis()
                                var ecg = DoubleArray(10000)
                                var ppg = DoubleArray(10000)

                                var listOfData = (activity as MainActivity).readDataLast1000(sharedPref.getLong("CalibrateTime1", 0L))
                                for (i in listOfData.indices) {

                                    ecg[i] = listOfData[i].getECG()
                                    ppg[i] = listOfData[i].getPPG()
                                }
                                listOfData = (activity as MainActivity).readDataLast1000(sharedPref.getLong("CalibrateTime2", 0L))
                                for (i in listOfData.indices) {

                                    ecg[i+1000] = listOfData[i].getECG()
                                    ppg[i+1000] = listOfData[i].getPPG()
                                }
                                listOfData = (activity as MainActivity).readDataLast1000(sharedPref.getLong("CalibrateTime3", 0L))
                                for (i in listOfData.indices) {

                                    ecg[i+2000] = listOfData[i].getECG()
                                    ppg[i+2000] = listOfData[i].getPPG()
                                }
                                listOfData = (activity as MainActivity).readDataLast1000(sharedPref.getLong("CalibrateTime4", 0L))
                                for (i in listOfData.indices) {

                                    ecg[i+3000] = listOfData[i].getECG()
                                    ppg[i+3000] = listOfData[i].getPPG()
                                }
                                listOfData = (activity as MainActivity).readDataLast1000(time)
                                for (i in listOfData.indices) {

                                    ecg[i+4000] = listOfData[i].getECG()
                                    ppg[i+4000] = listOfData[i].getPPG()
                                }

                                var realDBP = doubleArrayOf(
                                    sharedPref.getInt("RealDBP1", 0).toDouble(),
                                    sharedPref.getInt("RealDBP2", 0).toDouble(),
                                    sharedPref.getInt("RealDBP3", 0).toDouble(),
                                    sharedPref.getInt("RealDBP4", 0).toDouble(),
                                    inputDBPSeekBar.getProgress().toDouble(), 0.0, 0.0, 0.0, 0.0, 0.0)

                                var realSBP = doubleArrayOf(
                                    sharedPref.getInt("RealSBP1", 0).toDouble(),
                                    sharedPref.getInt("RealSBP2", 0).toDouble(),
                                    sharedPref.getInt("RealSBP3", 0).toDouble(),
                                    sharedPref.getInt("RealSBP4", 0).toDouble(),
                                    inputSBPSeekBar.getProgress().toDouble())


                                (activity as MainActivity).calibrate_wrapper(ecg, ppg, realDBP, realSBP)

                                editor.putLong("CalibrateTime1", 0L)
                                editor.putLong("CalibrateTime2", 0L)
                                editor.putLong("CalibrateTime3", 0L)
                                editor.putLong("CalibrateTime4", 0L)
                                editor.putInt("RealDBP1", 0)
                                editor.putInt("RealSBP1", 0)
                                editor.putInt("RealDBP2", 0)
                                editor.putInt("RealSBP2", 0)
                                editor.putInt("RealDBP3", 0)
                                editor.putInt("RealSBP3", 0)
                                editor.putInt("RealDBP4", 0)
                                editor.putInt("RealSBP4", 0)
                                editor.putInt("CalibrateTotal", 0)
                                editor.apply()

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