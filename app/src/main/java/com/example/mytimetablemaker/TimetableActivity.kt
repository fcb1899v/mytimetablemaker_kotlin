package com.example.mytimetablemaker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mytimetablemaker.databinding.ActivityTimetableBinding
import java.io.InputStream

class TimetableActivity: AppCompatActivity() {

    //フラグメントから渡されるデータの初期化
    private var goOrBack: String = ""
    private var lineNumber: Int = 0
    private var currentDay: Int = 1

    //ViewBinding
    private lateinit var binding: ActivityTimetableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimetableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //フラグメントから受け渡されたデータ
        val intentGoOrBack = "goorback"
        val intentLineNumber = "linenumber"
        val intentCurrentDay = "currentday"

        //時刻表の時刻表示のTextViewを配列として初期取得
        val tableMinutes: Array<TextView> = arrayOf(
            binding.tableMinutes04, binding.tableMinutes05, binding.tableMinutes06, binding.tableMinutes07,
            binding.tableMinutes08, binding.tableMinutes09, binding.tableMinutes10, binding.tableMinutes11,
            binding.tableMinutes12, binding.tableMinutes13, binding.tableMinutes14, binding.tableMinutes15,
            binding.tableMinutes16, binding.tableMinutes17, binding.tableMinutes18, binding.tableMinutes19,
            binding.tableMinutes20, binding.tableMinutes21, binding.tableMinutes22, binding.tableMinutes23,
            binding.tableMinutes24, binding.tableMinutes25
        )

        //フラグメントから渡されたデータ
        intent?.apply {
            goOrBack = getStringExtra(intentGoOrBack).toString()
            lineNumber = getIntExtra(intentLineNumber, 1)
            currentDay = getIntExtra(intentCurrentDay, 1)
        }

        //AppBarの表示設定
        supportActionBar?.apply{
            elevation = 0.0F
            setDisplayHomeAsUpEnabled(true)
        }

        //時刻表のタイトル
        binding.fromStation.text = goOrBack.departStation(lineNumber)
        val lineName: String = goOrBack.lineName(lineNumber)
        val arriveStation: String = goOrBack.arriveStation(lineNumber)
        val timetableTitle: String = lineName.timeTableTitle(arriveStation)
        binding.toStation.text = timetableTitle

        //平日または土日の表示
        binding.tableDay.apply{
            text = currentDay.dayOrEndString(getString(R.string.weekend), getString(R.string.weekday))
            setTextColor(currentDay.dayOrEndColor(Color.RED, Color.WHITE))
        }

        //平日と土日祝の表示の切替
        binding.dayButton.apply{
            text = currentDay.dayOrEndString(R.string.weekday.strings, R.string.weekend.strings)
            setTextColor(currentDay.dayOrEndColor(R.string.primaryDark.setColor, Color.RED))
            setOnClickListener {
                val currentDay = if (binding.dayButton.text == getString(R.string.weekday)) 1 else 0
                binding.tableDay.apply {
                    text = currentDay.dayOrEndString(R.string.weekend.strings, R.string.weekday.strings)
                    setTextColor(currentDay.dayOrEndColor(Color.RED, Color.WHITE))
                }
                binding.dayButton.apply {
                    text = currentDay.dayOrEndString(R.string.weekday.strings, R.string.weekend.strings)
                    setTextColor(currentDay.dayOrEndColor(R.string.primaryDark.setColor, Color.RED))
                }
                for (i: Int in 0..21) {
                    tableMinutes[i].text = goOrBack.getTimetableStringArray(lineNumber, currentDay)[i]
                }
            }
        }

        //時刻表の時刻の表示および設定
        for (i: Int in 0..21) {
            tableMinutes[i].apply{
                text = goOrBack.getTimetableStringArray(lineNumber, currentDay)[i]
                setOnClickListener {
                    val dayNumber: Int = when (currentDay) { 0, 6 -> 0 else -> 1 }
                    Timetable(context, goOrBack, dayNumber).makeTimetableDialog (tableMinutes[i], lineNumber, i + 4, tableMinutes)
                }
            }
        }

        //写真撮影
        binding.pictureSelectButton.setOnClickListener {
            getImageLauncher.launch("image/*")
        }
    }

    private val getImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val inputStream: InputStream? = contentResolver?.openInputStream(uri)
            val picture: Bitmap = BitmapFactory.decodeStream(inputStream)
            val pictureView: ImageView = binding.PictureView
            pictureView.setImageBitmap(picture)
        } else {
            // 画像選択がキャンセルされた
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }
}