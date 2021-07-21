package com.example.chironsolutions.ui.dashboard

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
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sin


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        return root

    }


    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?) {

        val graphView = idGraphView
//        var series = LineGraphSeries(
//            arrayOf<DataPoint>(
//                DataPoint(x, y),
//                DataPoint(x+0.1, y+5),
//                DataPoint(x+0.2, y+3),
//                DataPoint(x+0.3, y+2),
//                DataPoint(x+0.4, y+6)
//            )
//        )
//        graphView.addSeries(series)

        var series = LineGraphSeries<DataPoint>()

        //var latestValue = (activity as MainActivity).readLastestData()
        // string = (activity as MainActivity).getDate(latestValue.getDate())

        var date: Date
        var y : Double = 0.0

        var startTime: Date
        var endTime: Date

        var listOfData = (activity as MainActivity).readDataLast24Hours()
        //List<UserDataModel>
        for (i in listOfData.indices) {
            listOfData[i].getDBP()
            listOfData[i].getDate()
        }

        startTime = Date(System.currentTimeMillis())
        for (i in 0..49)
        {
            date = Date(System.currentTimeMillis())
            y = sin(5.0*i) + 80

            series.appendData(DataPoint(date,y), true, 51)

        }
        endTime = Date(System.currentTimeMillis())

        graphView.getGridLabelRenderer().setLabelFormatter(DateAsXAxisLabelFormatter(getActivity(), SimpleDateFormat("hh:mm:ss a")));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
        graphView.getGridLabelRenderer().setHumanRounding(false)

        graphView.getViewport().setYAxisBoundsManual(true)
        graphView.getViewport().setMinY(0.0)
        graphView.getViewport().setMaxY(120.0)

        graphView.getViewport().setMinX(startTime.getTime().toDouble())
        graphView.getViewport().setMaxX(endTime.getTime().toDouble())
        graphView.getViewport().setXAxisBoundsManual(true)
        graphView.getGridLabelRenderer().setLabelsSpace(1)
        graphView.getGridLabelRenderer().setLabelHorizontalHeight(40)

        //graphView.viewport.setScalableY(true)
        series.setTitle("Sample Diastolic BP")
        graphView.addSeries(series)

        graphView.setTitle("Blood Pressure");
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Blood Pressure (mmHg)");
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Time");
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
