package com.example.chironsolutions.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.chironsolutions.DatabaseHandler
import com.example.chironsolutions.MainActivity
import com.example.chironsolutions.databinding.FragmentSettingsBinding
import kotlinx.android.synthetic.main.fragment_settings.*

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

        // set on-click listener
        btnClearUserData.setOnClickListener { view ->

            var dataBaseHandler = DatabaseHandler(activity as MainActivity)
            dataBaseHandler.deleteAll()
            (activity as MainActivity).DataEntry(-1, 0.0, 0.0, 0.0, 0.0, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}