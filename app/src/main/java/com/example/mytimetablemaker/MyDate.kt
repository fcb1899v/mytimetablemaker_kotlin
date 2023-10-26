package com.example.mytimetablemaker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat.getBestDateTimePattern
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MyDate {

    //
    private fun getLocalizeDateString(date: Date, skeleton: String): String {
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

    fun getCurrentDate(dateButton: Button, timeButton: Button) {
        dateButton.text = getLocalizeDateString(Date(), "EEEMMMdyyyy")
        timeButton.text = getLocalizeDateString(Date(), "HH:mm:ss")
    }


    //＜日付の設定＞
    //(a)標準的なDatePickerDialogを表示する関数
    fun setDatePickerDialog(textview: TextView, context: Context, isTimeStart: Boolean) {
        if (!isTimeStart) {
            val inputDate: Calendar = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
                inputDate.apply {
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.MONTH, month)
                    set(Calendar.YEAR, year)
                }
                textview.text = getLocalizeDateString(inputDate.time, "EEEMMMdyyyy")
            }
            DatePickerDialog(context, dateSetListener, inputDate.get(Calendar.YEAR), inputDate.get(
                Calendar.MONTH
            ), inputDate.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    //＜日時の設定＞
    //(b)標準的なTimePickerDialogを表示する関数
    fun setTimePickerDialog(textview: TextView, context: Context, isTimeStart: Boolean) {
        if (!isTimeStart) {
            val inputTime: Calendar = Calendar.getInstance()
            val timeSetListener =
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hour: Int, minute: Int ->
                    inputTime.apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                    }
                    textview.text = getLocalizeDateString(inputTime.time, "HH:mm:ss")
                }
            TimePickerDialog(
                context, timeSetListener, inputTime.get(Calendar.HOUR_OF_DAY), inputTime.get(
                    Calendar.MINUTE
                ), true
            ).show()
        }
    }
}