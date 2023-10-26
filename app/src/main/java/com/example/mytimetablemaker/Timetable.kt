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

    private fun setEditNumber2(edittext: EditText) {
        edittext.apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(2))
            setHint(R.string.inputTimeHint)
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            textSize = 24F
        }
    }

    //時刻表に2桁の数字を入力するDialogの表示する関数
    fun makeTimetableDialog (textview: TextView, linenumber: Int, hour: Int, textViewArray: Array<TextView>) {
        AlertDialog.Builder(context).apply {
            val key = goOrBack.timetableKey(linenumber, hour, currentDay)
            val edittext = EditText(context)
            setEditNumber2(edittext)
            setTitle(R.string.inputTime.strings)
            setView(edittext)
            setNegativeButton(R.string.add) { _, _ ->
                val addedText = textview.text.toString().addTime(edittext.text.toString())
                myPreference.prefSaveText(key, addedText)
                textview.text = addedText
                makeTimetableDialog (textview, linenumber, hour, textViewArray)
            }
            setPositiveButton(R.string.delete) { _, _ ->
                val deletedText = textview.text.toString().deleteTime(edittext.text.toString())
                myPreference.prefSaveText(key, deletedText)
                textview.text = deletedText
                makeTimetableDialog (textview, linenumber, hour, textViewArray)
            }
            setNeutralButton(R.string.copyTime) { _, _ ->
                setChoiceListItemsDialog(textview, key, linenumber, hour, textViewArray)
            }
            show()
        }
    }

    //★リストを選択するDialogを表示する関数
    private fun setChoiceListItemsDialog(textview: TextView, key: String, linenumber: Int, hour: Int, textViewArray: Array<TextView>) {
        AlertDialog.Builder(context).apply {
            var selectNumber = 0
            setTitle(R.string.copyTime.strings)
            setSingleChoiceItems(timetableListArray(hour), 0) { _, which ->
                selectNumber = which
            }
            setPositiveButton(R.string.ok) { _, _ ->
                myPreference.prefSaveText(key, timetableValueArray(linenumber, hour)[selectNumber])
                textview.text = timetableValueArray(linenumber, hour)[selectNumber]
                if (selectNumber in 2..5) {
                    allCopyTimetableDialog(textViewArray, selectNumber, linenumber)
                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    private fun allCopyTimetableDialog(textViewArray: Array<TextView>, selectNumber: Int, linenumber: Int) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.copyAllTime.strings)
            setNegativeButton(R.string.yes) { _, _ ->
                for (hour in 4..25) {
                    val selectKey: String = goOrBack.timetableKey(linenumber, hour, currentDay)
                    val copyKey: String = allCopyKey(linenumber, hour, selectNumber)
                    myPreference.prefSaveText(selectKey, myPreference.savedText(copyKey, ""))
                    textViewArray[hour - 4].text = myPreference.savedText(copyKey, "")
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
            currentDay.weekdayString,
            R.string.routeOtherLine.strings + "1",
            R.string.routeOtherLine.strings + "2",
            R.string.routeOtherLine.strings + "3"
        )

    private fun timetableValueArray(linenumber: Int, hour: Int): Array<String> =
        arrayOf(
            myPreference.timeTableString(goOrBack, linenumber, hour - 1, currentDay),
            myPreference.timeTableString(goOrBack, linenumber, hour + 1, currentDay),
            myPreference.timeTableString(goOrBack, linenumber, hour, currentDay.otherDay),
            myPreference.timeTableString(goOrBack.otherGoOrBack, 0, hour, currentDay),
            myPreference.timeTableString(goOrBack.otherGoOrBack, 1, hour, currentDay),
            myPreference.timeTableString(goOrBack.otherGoOrBack, 2, hour, currentDay)
        )

    private fun allCopyKey(linenumber: Int, hour: Int, selectNumber: Int): String =
        when(selectNumber){
            3 -> goOrBack.otherGoOrBack.timetableKey(0, hour, currentDay)
            4 -> goOrBack.otherGoOrBack.timetableKey(1, hour, currentDay)
            5 -> goOrBack.otherGoOrBack.timetableKey(2, hour, currentDay)
            else -> goOrBack.timetableKey(linenumber, hour, currentDay.otherDay)
        }
}