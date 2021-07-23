package com.example.chironsolutions.ui.dashboard

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
import com.example.chironsolutions.databinding.FragmentDashboardBinding
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.sin


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

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
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {

        })

        requireActivity().applicationContext.registerReceiver(mReceiver, filter)

        return root

    }

    var series = LineGraphSeries<DataPoint>()
    lateinit var graphView : GraphView
    var lastIndex : Int = 0

    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?) {


        graphView = idGraphView

        var listOfData = (activity as MainActivity).readDataLast24Hours()
        var startTime = Date(listOfData[0].getDate())
        var endTime = Date(listOfData[listOfData.lastIndex].getDate())
        for (i in listOfData.indices) {

            series.appendData(DataPoint(Date(listOfData[i].getDate()),listOfData[i].getDBP()), true, 86400000)
        }
        lastIndex = listOfData.size

        graphView.getGridLabelRenderer().setLabelFormatter(DateAsXAxisLabelFormatter(getActivity(), SimpleDateFormat("hh:mm:ss a")));
        //graphView.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
        graphView.getGridLabelRenderer().setHumanRounding(false)

        graphView.getViewport().setYAxisBoundsManual(true)
        graphView.getViewport().setMinY(0.0)
        graphView.getViewport().setMaxY(120.0)

        graphView.getViewport().setMinX(startTime.getTime().toDouble())
        graphView.getViewport().setMaxX(endTime.getTime().toDouble())
        graphView.getViewport().setXAxisBoundsManual(true)
        //graphView.getGridLabelRenderer().setLabelsSpace(1)
        //graphView.getGridLabelRenderer().setLabelHorizontalHeight(40)
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(60)


        //graphView.viewport.setScalableY(true)
        series.setTitle("Sample Diastolic BP")
        graphView.addSeries(series)

        graphView.setTitle("Blood Pressure");
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Blood Pressure (mmHg)");
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Time");
    }

    fun updateValue(){

//        var latestValue = (activity as MainActivity).readDataLatest()
//        series.appendData(DataPoint(Date(latestValue.getDate()),latestValue.getDBP()), true, 86400000)
//        graphView.getViewport().setMaxX(Date(latestValue.getDate()).getTime().toDouble())

        var listOfData = (activity as MainActivity).readDataLast24Hours()
        var endTime = Date(listOfData[listOfData.lastIndex].getDate())
        for (i in lastIndex until listOfData.size) {

            series.appendData(DataPoint(Date(listOfData[i].getDate()),listOfData[i].getDBP()), true, 86400000)
        }
        lastIndex = listOfData.size
        graphView.getViewport().setMaxX(endTime.getTime().toDouble())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        requireActivity().applicationContext.unregisterReceiver(mReceiver)
    }


}
