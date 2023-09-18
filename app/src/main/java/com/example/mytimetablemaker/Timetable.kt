package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView

class Timetable (
    private val context: Context,
    private val goOrBack: String,
    private val currentDay: Int
){

    private val myPreference = MyPreference(context)

    //EditTextの書式設定（2桁の数字、中央配置、改行不可、黒色、サイズ24、指定したhintを表示）
    private fun setEditNumber2(edittext: EditText, hint: String) {
        edittext.apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(2))
            setHint(hint)
            inputType = InputType.TYPE_CLASS_TEXT
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            textSize = 24F
        }
    }

    //時刻表に2桁の数字を入力するDialogの表示する関数
    fun makeTimetableDialog (textview: TextView, linenumber: Int, hour: Int, textViewArray: Array<TextView>) {
        val edittext = EditText(context)
        val title1: String = R.string.inputTime.strings
        val title2: String = R.string.copyTime.strings
        val key = "${goOrBack}timetable${linenumber + 1}hour${hour.addZeroTime}${currentDay.weekDayOrEnd}"
        val hint: String = R.string.inputTimeHint.strings
        val currentText: String = textview.text.toString()
        val arraylist: Array<String> = timetableListArray(hour)
        val arrayValue: Array<String> = timetableValueArray(linenumber, hour)
        setEditNumber2(edittext, hint)
        AlertDialog.Builder(context).apply {
            setTitle(title1)
            setView(edittext)
            setNegativeButton(R.string.add) { _, _ ->
                myPreference.prefSaveText(key, currentText.addTime(edittext.text.toString()))
                textview.text = currentText.addTime(edittext.text.toString())
                makeTimetableDialog (textview, linenumber, hour, textViewArray)
            }
            setPositiveButton(R.string.delete) { _, _ ->
                myPreference.prefSaveText(key, currentText.deleteTime(edittext.text.toString()))
                textview.text = currentText.deleteTime(edittext.text.toString())
                makeTimetableDialog (textview, linenumber, hour, textViewArray)
            }
            setNeutralButton(R.string.copyTime) { _, _ ->
                setChoiceListItemsDialog(textview, key, title2, linenumber, arraylist, arrayValue, textViewArray)
            }
            show()
        }
    }

    //★リストを選択するDialogを表示する関数
    private fun setChoiceListItemsDialog(
        textview: TextView, key: String, title: String, linenumber: Int,
        arraylist: Array<String>, arrayValue: Array<String>, textViewArray: Array<TextView>,
    ) {
        var selectNumber = 0
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setSingleChoiceItems(arraylist, 0) { _, which ->
                selectNumber = which
                myPreference.prefSaveText(key, arrayValue[which])
                textview.text = arrayValue[which]
            }
            setNegativeButton(R.string.cancel) { _, _ ->
            }
            setPositiveButton(R.string.ok) { _, _ ->
                if (selectNumber in 2..5) {
                    allCopyTimetableDialog(textViewArray, selectNumber, linenumber)
                }
            }
            show()
        }
    }

    private fun allCopyTimetableDialog(textViewArray: Array<TextView>, selectNumber: Int, linenumber: Int) {
        val title: String = R.string.copyAllTime.strings
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setNegativeButton(R.string.yes) { _, _ ->
                for (hour in 4..25) {
                    val selectKey: String = goOrBack.timeTableKey(linenumber, hour, currentDay)
                    val copyKey: String = allCopyKey(linenumber, hour, selectNumber)
                    myPreference.prefSaveText(selectKey, copyKey.savedText(""))
                    textViewArray[hour - 4].text = copyKey.savedText("")
                }
            }
            setPositiveButton(R.string.no, null)
            show()
        }
    }

    private fun timetableListArray(hour: Int): Array<String> =
        arrayOf(
            "${hour - 1}${R.string.jidai.strings}",
            "${hour + 1}${R.string.jidai.strings}",
            currentDay.dayOrEndString(R.string.weekday.strings, R.string.weekend.strings),
            R.string.routeOtherLine.strings + "1",
            R.string.routeOtherLine.strings + "2",
            R.string.routeOtherLine.strings + "3"
        )

    private fun timetableValueArray(linenumber: Int, hour: Int): Array<String> =
        arrayOf(
            goOrBack.timeTableString(linenumber, hour - 1, currentDay),
            goOrBack.timeTableString(linenumber, hour + 1, currentDay),
            goOrBack.timeTableString(linenumber, hour, currentDay.otherDay),
            goOrBack.otherGoOrBack.timeTableString(0, hour, currentDay),
            goOrBack.otherGoOrBack.timeTableString(1, hour, currentDay),
            goOrBack.otherGoOrBack.timeTableString(2, hour, currentDay)
        )

    private fun allCopyKey(linenumber: Int, hour: Int, selectNumber: Int): String =
        when(selectNumber){
            3 -> goOrBack.otherGoOrBack.timeTableKey(0, hour, currentDay)
            4 -> goOrBack.otherGoOrBack.timeTableKey(1, hour, currentDay)
            5 -> goOrBack.otherGoOrBack.timeTableKey(2, hour, currentDay)
            else -> goOrBack.timeTableKey(linenumber, hour, currentDay.otherDay)
        }
}