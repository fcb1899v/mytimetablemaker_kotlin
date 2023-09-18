package com.example.mytimetablemaker

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat.getBestDateTimePattern
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MyDate {

    //　日時をローカライズ表示のStringで取得する関数
    fun localizeDateString(date: Date, skeleton: String): String {
        val local: Locale = Locale.getDefault()
        val format: String = getBestDateTimePattern(local, skeleton)
        val dateFormat = SimpleDateFormat(format, local)
        return dateFormat.format(date)
    }

    //　日時をローカライズ表示のDateで取得する関数
    fun getLocalizeDate(date: String, skeleton: String): Date? {
        val local: Locale = Locale.getDefault()
        val format: String = getBestDateTimePattern(local, skeleton)
        val dateFormat = SimpleDateFormat(format, local)
        return dateFormat.parse(date)
    }

    //＜日付の設定＞
    //(a)標準的なDatePickerDialogを表示する関数
    fun setDatePickerDialog(textview: TextView, context: Context) {
        val inputDate: Calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            inputDate.apply {
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
            }
            textview.text = localizeDateString(inputDate.time, "EEEMMMdyyyy")
        }
        DatePickerDialog(context, dateSetListener, inputDate.get(Calendar.YEAR), inputDate.get(
            Calendar.MONTH
        ), inputDate.get(Calendar.DAY_OF_MONTH))
            .show()
    }

    //＜日時の設定＞
    //(b)標準的なTimePickerDialogを表示する関数
    @SuppressLint("SetTextI18n")
    fun setTimePickerDialog(textview: TextView, context: Context) {
        val inputTime: Calendar = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _: TimePicker, hour: Int, minute: Int ->
            inputTime.apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            textview.text = localizeDateString(inputTime.time, "HH:mm") + ":00"
        }
        TimePickerDialog(context, timeSetListener, inputTime.get(Calendar.HOUR_OF_DAY), inputTime.get(
            Calendar.MINUTE
        ), true)
            .show()
    }
}