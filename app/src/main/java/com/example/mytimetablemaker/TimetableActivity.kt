package com.example.mytimetablemaker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mytimetablemaker.databinding.ActivityTimetableBinding
import java.io.InputStream

// Activity for displaying and editing timetables
class TimetableActivity: AppCompatActivity() {

    // Initialize data passed from fragment
    private var goOrBack: String = ""
    private var lineNumber: Int = 0
    private var currentDay: Int = 1

    // ViewBinding for accessing views
    private lateinit var binding: ActivityTimetableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewBinding
        binding = ActivityTimetableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure AppBar display settings
        supportActionBar?.apply{
            elevation = 0.0F
            setDisplayHomeAsUpEnabled(true)
        }

        val myPreference = MyPreference(this)

        // Intent keys for data passed from fragment
        val intentGoOrBack = "goorback"
        val intentLineNumber = "linenumber"
        val intentCurrentDay = "currentday"

        // Extract data passed from fragment
        intent?.apply {
            goOrBack = getStringExtra(intentGoOrBack).toString()
            lineNumber = getIntExtra(intentLineNumber, 1)
            currentDay = getIntExtra(intentCurrentDay, 1)
        }

        // Initialize timetable time display TextViews as array
        val tableMinutes: Array<TextView> = arrayOf(
            binding.tableMinutes04, binding.tableMinutes05, binding.tableMinutes06, binding.tableMinutes07,
            binding.tableMinutes08, binding.tableMinutes09, binding.tableMinutes10, binding.tableMinutes11,
            binding.tableMinutes12, binding.tableMinutes13, binding.tableMinutes14, binding.tableMinutes15,
            binding.tableMinutes16, binding.tableMinutes17, binding.tableMinutes18, binding.tableMinutes19,
            binding.tableMinutes20, binding.tableMinutes21, binding.tableMinutes22, binding.tableMinutes23,
            binding.tableMinutes24, binding.tableMinutes25
        )

        // Set timetable title
        binding.fromStation.text = myPreference.departStation(goOrBack, lineNumber)
        binding.toStation.text = myPreference.lineName(goOrBack, lineNumber).timeTableTitle(myPreference.arriveStation(goOrBack, lineNumber))

        // Display weekday or weekend
        binding.tableDay.apply {
            text = currentDay.weekdayString
            setTextColor(currentDay.weekdayTableColor)
        }

        // Toggle between weekday and weekend display
        binding.dayButton.apply {
            text = currentDay.weekendString
            setTextColor(currentDay.weekendButtonColor)
            setOnClickListener {
                val currentDay = if (binding.dayButton.text == getString(R.string.weekday)) 1 else 0
                binding.tableDay.apply {
                    text = currentDay.weekdayString
                    setTextColor(currentDay.weekdayTableColor)
                }
                binding.dayButton.apply {
                    text = currentDay.weekendString
                    setTextColor(currentDay.weekendButtonColor)
                }
                // Update timetable display
                for (i: Int in 0..21) {
                    tableMinutes[i].apply {
                        text = myPreference.getTimetableStringArray(goOrBack, lineNumber, currentDay)[i]
                    }
                }
            }
        }

        // Display and configure timetable times
        for (i: Int in 0..21) {
            tableMinutes[i].apply {
                text = myPreference.getTimetableStringArray(goOrBack, lineNumber, currentDay)[i]
                setOnClickListener {
                    val dayNumber: Int = when (currentDay) { 0, 6 -> 0 else -> 1 }
                    Timetable(context, goOrBack, dayNumber).makeTimetableDialog (tableMinutes[i], lineNumber, i + 4, tableMinutes)
                }
            }
        }

        // Photo capture functionality
        binding.pictureSelectButton.setOnClickListener {
            getImageLauncher.launch("image/*")
        }
    }

    // Image picker launcher for photo selection
    private val getImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val inputStream: InputStream? = contentResolver?.openInputStream(uri)
            val picture: Bitmap = BitmapFactory.decodeStream(inputStream)
            val pictureView: ImageView = binding.PictureView
            pictureView.setImageBitmap(picture)
        } else {
            // Image selection was cancelled
        }
    }

    // Handle back button press in action bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }
}