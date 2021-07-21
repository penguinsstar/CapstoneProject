package com.example.chironsolutions.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.chironsolutions.MainActivity
import com.example.chironsolutions.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.math.roundToInt


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if ("new_data" == intent.action) {
                updateValue()
            }
        }
    }
    val filter = IntentFilter("new_data")

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        homeViewModel.text.observe(viewLifecycleOwner, Observer {

        })

        requireActivity().applicationContext.registerReceiver(mReceiver, filter)

        return root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?) {

        updateValue()
    }

    fun updateValue(){

        val guage = idGauge
        val guageText = idGuageNumber

        var latestValue = (activity as MainActivity).readLastestData()
        guage.setValue((latestValue.getDBP()).roundToInt())
        guageText.setText(guage.getValue().toString());
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        requireActivity().applicationContext.unregisterReceiver(mReceiver)
    }


}