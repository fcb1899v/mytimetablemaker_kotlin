package com.example.mytimetablemaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mytimetablemaker.databinding.FragmentGoorback1Binding

// Fragment for displaying route information (first route)
class RouteFragment1 : Fragment() {

    // Parameter keys from MainActivity
    private val argCurrentDay = "currentday"
    private val argCurrentHHMMSS = "currenthhmmss"
    private val argGoOrBack1 = "goorback1"

    // Data received from MainActivity
    private var currentDay: Int = 0
    private var currentHHMMSS: Int = 0
    private var goOrBack1: String = ""

    // ViewBinding for accessing views
    private lateinit var binding: FragmentGoorback1Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGoorback1Binding.inflate(layoutInflater, container, false)

        // Get data from MainActivity
        val currentDay: Int = currentDay
        val currentHHMMSS: Int = currentHHMMSS
        val goOrBack1: String = goOrBack1

        // Initialize route fragment manager
        val myRouteFragment = MyRouteFragment(requireContext(), currentDay, currentHHMMSS)

        // Define view binding for text views
        val textView1: Map<String, Array<TextView>> = mapOf(
            "depart" to arrayOf(binding.stationName110, binding.stationName111, binding.stationName113, binding.stationName115),
            "arrive" to arrayOf(binding.stationName11e, binding.stationName112, binding.stationName114, binding.stationName116),
            "line" to arrayOf(binding.lineName111, binding.lineName113, binding.lineName115),
            "transport" to arrayOf(binding.lineName11e, binding.lineName110, binding.lineName112, binding.lineName114),
            "departTime" to  arrayOf(binding.startTime110, binding.startTime111, binding.startTime113, binding.startTime115),
            "arriveTime" to  arrayOf(binding.startTime11e, binding.startTime112, binding.startTime114, binding.startTime116),
            "countdown" to arrayOf(binding.countdown11),
        )
        // Define view binding for line views
        val view1: Map<String, Array<View>> = mapOf(
            "line" to arrayOf(binding.lineLine111, binding.lineLine113, binding.lineLine115),
            "transit" to arrayOf(binding.lineLine11e, binding.lineLine110, binding.lineLine112, binding.lineLine114),
        )
        // Define layout views for route display
        val layoutView1: Array<LinearLayout> =
            arrayOf(binding.linearlayout112, binding.linearlayout113)

        // Initialize route display
        myRouteFragment.mainFun(goOrBack1, textView1, view1, layoutView1)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Extract arguments passed from MainActivity
        arguments?.let {
            currentDay = it.getInt(argCurrentDay)
            currentHHMMSS = it.getInt(argCurrentHHMMSS)
            goOrBack1 = it.getString(argGoOrBack1)!!
        }
    }

    companion object {
        // Create new instance with parameters
        @JvmStatic
        fun newInstance(currentDay: Int, currentHHMMSS: Int, goOrBack1: String) =
            RouteFragment1().apply {
                arguments = Bundle().apply {
                    putInt(argCurrentDay, currentDay)
                    putInt(argCurrentHHMMSS, currentHHMMSS)
                    putString(argGoOrBack1, goOrBack1)
                }
            }
    }
}