package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView

class MySettings (
    private val context: Context
){
    private val myPreference = MyPreference(context)

    //EditTextの書式設定（10文字に制限、中央配置、改行不可、黒色、サイズ20）
    private fun setEditText20(edittext: EditText) {
        edittext.apply{
            filters = arrayOf(InputFilter.LengthFilter(20))
            setHint(R.string.character10Hint)
            inputType = InputType.TYPE_CLASS_TEXT
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            textSize = 20F
        }
    }

    //EditTextの書式設定（2桁の数字、中央配置、改行不可、黒色、サイズ24、指定したhintを表示）
    private fun setEditNumber2(edittext: EditText) {
        edittext.apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(2))
            setHint(R.string.minutes2Hint)
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
            textSize = 24F
        }
    }

    //Normal edit text dialog
    private fun setEditTextAlertDialog(textview: TextView, title: String, key: String, isSettings: Boolean) {
        AlertDialog.Builder(context).apply{
            val edittext = EditText(context)
            setEditText20(edittext)
            setTitle(title)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(key, edittext.text.toString())
                    textview.apply{
                        text = edittext.text.toString()
                        if (isSettings) setTextColor(edittext.text.toString().settingsTextColor)
                    }
                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //Setting change line alert dialog
    fun setChangeLineDialog(goOrBack: String, textview: TextView) {
        var selected = 0
        AlertDialog.Builder(context).apply {
            setTitle(R.string.settingsChangeLineTitle.strings)
            setSingleChoiceItems(R.array.changeLineList.arrayStrings, 0) { _, which ->
                selected = which
            }
            setPositiveButton(R.string.register) { _,_ ->
                myPreference.prefSaveText(goOrBack.changeLineKey, R.array.changeLineValue.arrayStrings[selected])
                textview.text = R.array.changeLineList.arrayStrings[selected]
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //Setting depart point alert dialog
    fun setDepartPointDialog(goOrBack: String, textview: TextView, isSettings: Boolean) =
        setEditTextAlertDialog(textview, R.string.settingDepartPointTitle.strings, goOrBack.departPointKey, isSettings)

    //Setting arrive point alert dialog
    fun setArrivePointDialog(goOrBack: String, textview: TextView, isSettings: Boolean) =
        setEditTextAlertDialog(textview, R.string.settingDestinationTitle.strings, goOrBack.arrivePointKey, isSettings)

    //Setting depart station alert dialog
    fun setDepartStationDialog(goOrBack: String, textview: TextView, i: Int, isSettings: Boolean) =
        setEditTextAlertDialog(textview, departStationTitle(i + 1), goOrBack.departStationKey(i + 1), isSettings)

    //Setting arrive station alert dialog
    fun setArriveStationDialog(goOrBack: String, textview: TextView, i: Int, isSettings: Boolean) =
        setEditTextAlertDialog(textview, arriveStationTitle(i + 1), goOrBack.arriveStationKey(i + 1), isSettings)

    //Setting line name alert dialog -> NeutralButton: Setting line color
    fun setLineNameDialog(goOrBack: String, textview: TextView, view: View?, i: Int) {
        val edittext = EditText(context)
        setEditText20(edittext)
        AlertDialog.Builder(context).apply {
            setTitle(lineNameTitle(i + 1))
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(goOrBack.lineNameKey(i + 1), edittext.text.toString())
                    textview.text = edittext.text.toString()
                }
            }
            setNeutralButton(R.string.settingLineColorButton) { _, _ ->
                if (edittext.text.toString() != "") {
                    myPreference.prefSaveText(goOrBack.lineNameKey(i + 1), edittext.text.toString())
                    textview.text = edittext.text.toString()
                }
                setChoiceColorAlertDialog(textview, view, goOrBack.lineColorKey(i + 1))
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //Setting line color alert dialog
    private fun setChoiceColorAlertDialog(textview: TextView, view: View?, key: String) {
        AlertDialog.Builder(context).apply {
            var selected = R.array.lineColorValue.arrayStrings[0]
            setTitle(R.string.settingLineColorTitle.strings)
            setSingleChoiceItems(R.array.lineColorList.arrayStrings, 0) { _, which ->
                selected = R.array.lineColorValue.arrayStrings[which]
            }
            setPositiveButton(R.string.register) { _,_ ->
                myPreference.prefSaveText(key, selected)
                textview.setTextColor(selected.setStringColor)
                view?.setBackgroundColor(selected.setStringColor)
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //Setting ride time alert dialog -> NeutralButton: Setting timetable
    fun setRideTimeDialog(goOrBack: String, textview: TextView?, i: Int, intent: Intent) {
        val edittext = EditText(context)
        setEditNumber2(edittext)
        AlertDialog.Builder(context).apply {
            setTitle(myPreference.lineName(goOrBack, i).rideTimeTitle)
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (!(edittext.text.toString().none)) {
                    myPreference.prefSaveText(goOrBack.rideTimeKey(i + 1), edittext.text.toString())
                    textview?.apply{
                        text = edittext.text.toString().addMinutes
                        setTextColor(edittext.text.toString().settingsTextColor)
                    }
                }
            }
            setNeutralButton(R.string.timetableTitle.strings) { _, _ ->
                if (!(edittext.text.toString().none)) {
                    myPreference.prefSaveText(goOrBack.rideTimeKey(i + 1), edittext.text.toString())
                    textview?.apply{
                        text = edittext.text.toString().addMinutes
                        setTextColor(edittext.text.toString().settingsTextColor)
                    }
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                Application.context.startActivity(intent)
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //Setting transportation alert dialog
    fun setTransportationDialog(goOrBack: String, textview: TextView, i: Int, isSettings: Boolean) {
        AlertDialog.Builder(context).apply {
            var selected = R.string.walking.strings
            setTitle(myPreference.transitStation(goOrBack, i).transportationTitle(i))
            setSingleChoiceItems(R.array.transportationList.arrayStrings, 0) { _, which ->
                selected = R.array.transportationList.arrayStrings[which]
            }
            setPositiveButton(R.string.register) { _, _ ->
                myPreference.prefSaveText(goOrBack.transportationKey(i), selected)
                textview.apply{
                    text = selected
                    if (isSettings) setTextColor(R.string.black.setColor)
                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //Setting transit time alert dialog
    fun setTransitTimeDialog(goOrBack: String, textview: TextView?, i: Int) {
        val edittext = EditText(context)
        setEditNumber2(edittext)
        AlertDialog.Builder(context).apply {
            setTitle(myPreference.transitStation(goOrBack, i).transitTimeTitle(i))
            setView(edittext)
            setPositiveButton(R.string.register) { _, _ ->
                if (!(edittext.text.toString().none)) {
                    myPreference.prefSaveText(goOrBack.transitTimeKey(i), edittext.text.toString())
                    textview?.apply{
                        text = edittext.text.toString().addMinutes
                        setTextColor(edittext.text.toString().settingsTextColor)
                    }
                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }
}