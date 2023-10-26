package com.example.mytimetablemaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mytimetablemaker.databinding.FragmentGoorback2Binding

class RouteFragment2 : Fragment() {

    //Parameter from MainActivity
    private val argCurrentDay = "currentday"
    private val argCurrentHHMMSS = "currenthhmmss"
    private val argGoOrBack2 = "goorback2"

    //Data from MainActivity
    private var currentDay: Int = 0
    private var currentHHMMSS: Int = 0
    private var goOrBack2: String = ""

    //ViewBinding
    private lateinit var binding: FragmentGoorback2Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGoorback2Binding.inflate(layoutInflater, container, false)

        //Data from MainActivity
        val currentDay: Int = currentDay
        val currentHHMMSS: Int = currentHHMMSS
        val goOrBack2: String = goOrBack2

        //Define class
        val myRouteFragment = MyRouteFragment(requireContext(), currentDay, currentHHMMSS)

        //Define view binding
        val textView2: Map<String, Array<TextView>> = mapOf(
            "depart" to arrayOf(binding.stationName120, binding.stationName121, binding.stationName123, binding.stationName125),
            "arrive" to arrayOf(binding.stationName12e, binding.stationName122, binding.stationName124, binding.stationName126),
            "line" to arrayOf(binding.lineName121, binding.lineName123, binding.lineName125),
            "transport" to arrayOf(binding.lineName12e, binding.lineName120, binding.lineName122, binding.lineName124),
            "departTime" to  arrayOf(binding.startTime120, binding.startTime121, binding.startTime123, binding.startTime125),
            "arriveTime" to  arrayOf(binding.startTime12e, binding.startTime122, binding.startTime124, binding.startTime126),
            "countdown" to arrayOf(binding.countdown12),
        )
        val view2: Map<String, Array<View>> = mapOf(
            "line" to arrayOf(binding.lineLine121, binding.lineLine123, binding.lineLine125),
            "transit" to arrayOf(binding.lineLine12e, binding.lineLine120, binding.lineLine122, binding.lineLine124),
        )
        val layoutView2: Array<LinearLayout> =
            arrayOf(binding.linearlayout122, binding.linearlayout123)

        myRouteFragment.mainFun(goOrBack2, textView2, view2, layoutView2)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentDay = it.getInt(argCurrentDay)
            currentHHMMSS = it.getInt(argCurrentHHMMSS)
            goOrBack2 = it.getString(argGoOrBack2)!!
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(currentDay: Int, currentHHMMSS: Int, goOrBack2: String) =
            RouteFragment2().apply {
                arguments = Bundle().apply {
                    putInt(argCurrentDay, currentDay)
                    putInt(argCurrentHHMMSS, currentHHMMSS)
                    putString(argGoOrBack2, goOrBack2)
                }
            }
    }
}
