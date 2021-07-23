package com.example.chironsolutions.ui.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
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
import kotlinx.android.synthetic.main.fragment_settings_calibrate_dialog.*


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

        val btnClearUserData = idBtnClearUserData
        val btnCalibrate = idBtnCalibrate

        // set on-click listener
        btnClearUserData.setOnClickListener { view ->

            var dataBaseHandler = DatabaseHandler(activity as MainActivity)
            dataBaseHandler.deleteAll()
            (activity as MainActivity).DataEntry(-1, 0.0, 0.0, 0.0, 0.0, 0)
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
                    //add code here
                    labelDBPValue.setText((seekBar.getProgress()+50).toString())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    //add code here
                    labelDBPValue.setText((seekBar.getProgress()+50).toString())
                }

                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    //add code here
                    labelDBPValue.setText((seekBar.getProgress()+50).toString())
                }
            }
            val SBPSeekBarListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    //add code here
                    labelSBPValue.setText((seekBar.getProgress()+80).toString())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    //add code here
                    labelSBPValue.setText((seekBar.getProgress()+80).toString())
                }

                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    //add code here
                    labelSBPValue.setText((seekBar.getProgress()+80).toString())
                }
            }
            inputDBPSeekBar.setOnSeekBarChangeListener(DBPSeekBarListener)
            inputSBPSeekBar.setOnSeekBarChangeListener(SBPSeekBarListener)

            builder.setMessage("Calibrating")
                .setPositiveButton("ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        // FIRE ZE MISSILES!
                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()


        } ?: throw IllegalStateException("Activity cannot be null")

    }

//    override fun onViewCreated(
//        view: View,
//        savedInstanceState: Bundle?){
//
//    }
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//
//    }


}