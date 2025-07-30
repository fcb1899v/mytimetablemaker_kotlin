package com.example.mytimetablemaker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.mytimetablemaker.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Main activity for the application
class MainActivity: AppCompatActivity() {

    // ViewBinding for accessing views
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Control splash screen manually
        val splashScreen = installSplashScreen()
        var isReady = false
        
        // Control splash screen exit
        splashScreen.setKeepOnScreenCondition { !isReady }
        
        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Execute initialization process asynchronously
        lifecycleScope.launch {
            initializeApp()
            setupUI()
            isReady = true
        }
    }
    
    // Initialize AdMob and other heavy operations
    private suspend fun initializeApp() {
        withContext(Dispatchers.Main) {
            // Initialize AdMob advertisement
            val admobClass = AdMob()
            admobClass.setAdMob(binding.admobView, this@MainActivity)
        }
    }
    
    // Setup UI components and event listeners
    private fun setupUI() {
        // Initialize preference and utility classes
        val myPreference = MyPreference(this)
        val myDate = MyDate()
        val calendar: Calendar = Calendar.getInstance()
        val handler = Handler(Looper.getMainLooper())

        // Define parameters for time and date handling
        var isTimeStart = true
        var currentDay = 0
        var currentHHMMSS = 0
        var goOrBack12 = arrayOf("back1", "back2")
        var runnable = Runnable {}

        // Update fragment views with current date and time
        fun getFragmentView() {
            // Update current date and time display
            myDate.getCurrentDate(binding.currentDate, binding.currentTime)
            val displayDate = myDate.getLocalizeDate(binding.currentDate.text.toString(), "EEEMMMdyyyy")!!
            val displayTime = myDate.getLocalizeDate(binding.currentTime.text.toString(), "HH:mm:ss")!!
            calendar.time = displayDate
            currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1
            currentHHMMSS = SimpleDateFormat("HHmmss", Locale.US).format(displayTime).toInt()
            
            // Update route fragments with new time and day
            supportFragmentManager.beginTransaction()
                .replace(R.id.RouteFragment1, RouteFragment1.newInstance(currentDay, currentHHMMSS, goOrBack12[0]))
                .commitAllowingStateLoss()
            supportFragmentManager.beginTransaction()
                .replace(R.id.RouteFragment2, RouteFragment2.newInstance(currentDay, currentHHMMSS, goOrBack12[1]))
                .commitAllowingStateLoss()
        }

        // Change between going out and returning home modes
        fun changeGoOrBack(goOrBack1: String, goOrBack2: String) {
            goOrBack12 = arrayOf(goOrBack1, goOrBack2)
            val back2display = myPreference.getRoute2Switch(goOrBack2)
            binding.RouteFragment2.visibility = if (back2display) { View.VISIBLE } else { View.GONE }
            binding.centerLine.visibility = if (back2display) { View.VISIBLE } else { View.GONE }
            supportFragmentManager.beginTransaction()
                .replace(R.id.RouteFragment1, RouteFragment1.newInstance(currentDay, currentHHMMSS, goOrBack1))
                .commitAllowingStateLoss()
            supportFragmentManager.beginTransaction()
                .replace(R.id.RouteFragment2, RouteFragment2.newInstance(currentDay, currentHHMMSS, goOrBack2))
                .commitAllowingStateLoss()
        }

        // Navigate to settings activity
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Date and time related settings

        // Time stop button setting (when time is not stopped)
        binding.timeStartButton.setOnClickListener {
            isTimeStart = setAccentColor(binding.timeStartButton, binding.timeStopButton, true)
        }
        // Time start button setting (when time is stopped)
        binding.timeStopButton.setOnClickListener {
            isTimeStart = setAccentColor(binding.timeStartButton, binding.timeStopButton, false)
        }

        // Date selection and display (when time is stopped)
        binding.currentDate.setOnClickListener {
            myDate.setDatePickerDialog(binding.currentDate, this, isTimeStart)
        }
        // Time selection and display (when time is stopped)
        binding.currentTime.setOnClickListener {
            myDate.setTimePickerDialog(binding.currentTime, this, isTimeStart)
        }

        // Display change when transitioning from going out screen to returning home screen
        binding.backButton.setOnClickListener {
            setAccentColor(binding.backButton, binding.goButton, true)
            changeGoOrBack("back1", "back2")
        }
        // Display change when transitioning from returning home screen to going out screen
        binding.goButton.setOnClickListener {
            setAccentColor(binding.backButton, binding.goButton, false)
            changeGoOrBack("go1", "go2")
        }

        // Periodic update of fragment views
        runnable = Runnable {
            if (isTimeStart) {
                getFragmentView()
            }
            handler.postDelayed(runnable, 1000)
        }
        handler.post(runnable)
    }

    // Set accent color for buttons (on/off state)
    private fun setAccentColor(onButton: Button, offButton: Button, isOn: Boolean): Boolean {
        onButton.setBackgroundResource(if (isOn) R.drawable.on_button else R.drawable.off_button)
        offButton.setBackgroundResource(if (isOn) R.drawable.off_button else R.drawable.on_button)
        return isOn
    }
}