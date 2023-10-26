package com.example.mytimetablemaker

import android.content.Context
import androidx.preference.PreferenceManager

class MyPreference (
    val context: Context,
){

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    private fun prefGetString(key: String, defaultValue: String): String? = prefs.getString(key, defaultValue)
    //private fun prefGetInt(key: String, defaultValue: Int): Int = prefs.getInt(key, defaultValue)
    fun prefGetBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)

    fun savedText(key: String, defaultValue: String): String =
        if (prefGetString(key, defaultValue) == null) defaultValue else prefGetString(key, defaultValue)!!
    private fun settingsSavedText(key: String): String = savedText(key, R.string.notSet.strings)
    private fun savedIntString(key: String): String = savedText(key, "0")

    private fun savedInt(key: String): Int = savedIntString(key).toInt()

    fun getRoute2Switch(goOrBack: String): Boolean = prefGetBoolean(goOrBack.route2Key, false)
    fun changeLine(goOrBack: String): Int = savedInt(goOrBack.changeLineKey)

    fun departPoint(goOrBack: String): String = savedText(goOrBack.departPointKey, goOrBack.departPointDefault)
    fun settingsDepartPoint(goOrBack: String): String = settingsSavedText(goOrBack.departPointKey)

    fun arrivePoint(goOrBack: String): String = savedText(goOrBack.arrivePointKey, goOrBack.arrivePointDefault)
    fun settingsArrivePoint(goOrBack: String): String = settingsSavedText(goOrBack.arrivePointKey)

    fun departStation(goOrBack: String, i: Int): String = savedText(goOrBack.departStationKey(i + 1), departStationDefault(i + 1))
    fun settingsDepartStation(goOrBack: String, i: Int): String = settingsSavedText(goOrBack.departStationKey(i + 1))
    fun departStationFirestore(goOrBack: String): Array<String> = (0..2).map{departStation(goOrBack, it)}.toTypedArray()

    fun arriveStation(goOrBack: String, i: Int): String = savedText(goOrBack.arriveStationKey(i + 1), arriveStationDefault(i + 1))
    fun settingsArriveStation(goOrBack: String, i: Int): String = settingsSavedText(goOrBack.arriveStationKey(i + 1))
    fun arriveStationFirestore(goOrBack: String): Array<String> = (0..2).map{arriveStation(goOrBack, it)}.toTypedArray()

    fun lineName(goOrBack: String, i: Int): String = savedText(goOrBack.lineNameKey(i + 1), lineNameDefault(i + 1))
    fun settingsLineName(goOrBack: String, i: Int): String = settingsSavedText(goOrBack.lineNameKey(i + 1))
    fun lineNameFirestore(goOrBack: String): Array<String> = (0..2).map{lineName(goOrBack, it)}.toTypedArray()

    fun lineColor(goOrBack: String, i: Int): String = savedText(goOrBack.lineColorKey(i + 1), R.string.colorAccent.strings)
    private fun settingsLineColor(goOrBack: String, i: Int): String = savedText(goOrBack.lineColorKey(i + 1), R.string.lightGray.strings)
    fun settingsLineColorInt(goOrBack: String,i: Int): Int = settingsLineColor(goOrBack, i).setStringColor
    fun lineColorFirestore(goOrBack: String): Array<String> = (0..2).map{lineColor(goOrBack, it)}.toTypedArray()

    private fun rideTime(goOrBack: String, i: Int): String = savedIntString(goOrBack.rideTimeKey(i + 1))
    fun settingsRideTime(goOrBack: String, i: Int): String = settingsSavedText(goOrBack.rideTimeKey(i + 1))
    fun rideTimeInt(goOrBack: String, i: Int): Int = savedInt(goOrBack.rideTimeKey(i + 1))
    fun rideTimeFirestore(goOrBack: String): Array<String> = (0..2).map{rideTime(goOrBack, it)}.toTypedArray()

    fun transportation(goOrBack: String, i: Int): String = savedText(goOrBack.transportationKey(i), R.string.walking.strings)
    fun settingsTransportation(goOrBack: String, i: Int): String = settingsSavedText(goOrBack.transportationKey(i))
    fun transportationFirestore(goOrBack: String): Array<String> = (0..3).map{transportation(goOrBack, it)}.toTypedArray()

    private fun transitTime(goOrBack: String, i: Int): String = savedIntString(goOrBack.transitTimeKey(i))
    fun transitTimeInt(goOrBack: String, i: Int): Int = savedInt(goOrBack.transitTimeKey(i))
    fun settingsTransitTime(goOrBack: String, i: Int): String = settingsSavedText(goOrBack.transitTimeKey(i))
    fun transitTimeFirestore(goOrBack: String): Array<String> = (0..3).map{transitTime(goOrBack, it)}.toTypedArray()

    fun timeTableString(goOrBack: String, i: Int, hour: Int, currentDay: Int): String =
        savedText(goOrBack.timetableKey(i, hour, currentDay),"").timeSorting.timeString
    fun timeTableArrayInt(goOrBack: String, i: Int, hour: Int, currentDay: Int): Array<Int> =
        savedText(goOrBack.timetableKey(i, hour, currentDay),"").timeSorting.timeArrayInt(hour)
    fun getTimetableStringArray(goOrBack: String, i: Int, currentDay: Int): Array<String> =
        (4..25).map{timeTableString(goOrBack, i, it, currentDay)}.toTypedArray()

    fun transitStation(goOrBack: String, i: Int): String = when (i) {
        0 -> arrivePoint(goOrBack)
        1 -> departPoint(goOrBack)
        else -> arriveStation(goOrBack,i - 2)
    }

    fun changeLineString(goOrBack: String): String = when (changeLine(goOrBack)) {
        1 -> R.string.once.strings
        2 -> R.string.twice.strings
        else -> R.string.zero.strings
    }

    fun prefSaveText(key: String, text: String) {
        prefs.edit().apply {
            putString("dummy", "dummy").apply()
            putString(key, text).apply()
        }
    }

    fun prefSaveBoolean(key: String, isChecked: Boolean) {
        prefs.edit().apply {
            putBoolean("dummy", true).apply()
            putBoolean(key, isChecked).apply()
        }
    }
}