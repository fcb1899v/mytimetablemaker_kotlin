package com.example.mytimetablemaker
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color.parseColor
import com.example.mytimetablemaker.Application.Companion.context
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

// Class to make Resources data accessible from anywhere
class Application: android.app.Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context = this
    }
}

// Resource access extensions
val Int.strings: String get() = context.getString(this)
val Int.arrayStrings: Array<String> get() = context.resources.getStringArray(this)
val Int.setColor: Int get() = parseColor(strings)
val String.setStringColor: Int get() = parseColor(this)
fun String?.nullToString(defaultValue: String): String = this ?: defaultValue
val Boolean?.nullToBoolean: Boolean get() = this ?: false

// Go or back string utilities
fun String.goOrBackString(backString: String, goString: String): String = when (this) { "back1", "back2" -> backString else -> goString }
fun String.changeWord(changeWord: String, i: Int):String = when (i) { 0 -> this else -> changeWord }
val String.getLastChar: Char get() = this[this.length - 1]
val String.exchange1and2: String get() = if (getLastChar == '2') "1" else "2"
val String.otherGoOrBack: String get() = "${this.substring(0, this.length - 1)}$exchange1and2"

// Shared Preference Key: This is goOrBack
val String.route2Key: String get() = "${this}switch"
val String.changeLineKey: String get() = "${this}changeline"
val String.departPointKey: String get() = goOrBackString("destination", "departurepoint")
val String.arrivePointKey: String get() = goOrBackString("departurepoint", "destination")
fun String.departStationKey(i: Int): String = "${this}departstation${i}"
fun String.arriveStationKey(i: Int): String = "${this}arrivalstation${i}"
fun String.lineNameKey(i: Int): String = "${this}linename${i}"
fun String.lineColorKey(i: Int): String = "${this}linecolor${i}"
fun String.rideTimeKey(i: Int): String = "${this}ridetime${i}"
fun String.transportationKey(i: Int): String = "${this}transportation${if (i == 0) "e" else i}"
fun String.transitTimeKey(i: Int): String = "${this}transittime${if (i == 0) "e" else i}"
fun String.timetableKey(linenumber: Int, hour: Int, day: Int): String = "${this}timetable${linenumber + 1}hour${hour.addZeroTime}${day.weekDayOrEnd}"

// Shared Preference Default: This is goOrBack
const val changeLineDefault = "0"
val String.departPointDefault: String get() = goOrBackString(R.string.office.strings, R.string.home.strings)
val String.arrivePointDefault: String get() = goOrBackString(R.string.home.strings, R.string.office.strings)
fun departStationDefault(i: Int): String = "${R.string.depSta.strings}${i}"
fun arriveStationDefault(i: Int): String = "${R.string.arrSta.strings}${i}"
fun lineNameDefault(i: Int): String = "${R.string.line.strings}${i}"
val lineColorDefault = R.color.accent.strings
val transportationDefault = R.string.walking.strings
const val rideTimeDefault = "0"
const val transitTimeDefault = "0"

// Alert Title generators
fun departStationTitle(i: Int) = "${R.string.settingStationName.strings}${R.string.departStation.strings}$i"
fun arriveStationTitle(i: Int) = "${R.string.settingStationName.strings}${R.string.arriveStation.strings}$i"
fun lineNameTitle(i: Int) = "${R.string.settingLineName.strings}${R.string.line.strings}$i"
/////This is transitStation
fun String.fromOrTo(i: Int): String = "${R.string.to.strings.changeWord(R.string.from.strings, i)}$this${R.string.he.strings.changeWord(R.string.kara.strings, i)}"
fun String.transportationTitle(i: Int): String = "${R.string.settingTransportation.strings}${fromOrTo(i)}"
fun String.transitTimeTitle(i: Int): String = "${R.string.settingTransitTime.strings}${fromOrTo(i)}"
/////This is lineName
val String.rideTimeTitle: String get() = "${R.string.settingRideTime.strings}$this"
fun String.timeTableTitle(arriveStation: String) = "($this ${R.string.colon.strings} $arriveStation${R.string.houmen.strings})"

// Not set utilities
val String.none: Boolean get() = when (this) { "", "0" -> true else -> false }
val String.settingsTextColor: Int get() = when (this) {
    R.string.notSet.strings, R.string.office.strings, R.string.home.strings -> R.color.gray
    else -> R.color.black
}
val String.addMinutes: String get() = when (this) {
    "", R.string.notSet.strings -> this
    else -> "$this ${R.string.minutesUnit.strings}"
}

// Login utilities
const val loginKey = "loggedin"
val goOrBackArray: Array<String> = arrayOf("back1", "go1", "back2", "go2")
val goOrBack2Array: Array<String> = arrayOf("back2", "go2")
fun accountTextArray(isLogin: Boolean): Array<String> = arrayOf(
    (if (isLogin) R.string.get_saved_data else R.string.get_data_after_login).strings,
    (if (isLogin) R.string.save_current_data else R.string.save_data_after_login).strings,
    R.string.logout.strings,
    R.string.delete_account.strings
)

// Firestore Path constants
const val usersColPath = "users"
const val goOrBackDocPath = "goorback"
const val timetableColPath = "timetable"
fun timetableDocPath(linenumber: Int, day: Int) = "timetable${linenumber + 1}${day.weekDayOrEnd}"

// Task Result utilities
fun Task<DocumentSnapshot>.taskResult(key: String, defaultValue: String) =
    this.result?.get(key).toString().nullToString(defaultValue)
fun Task<DocumentSnapshot>.taskResultBoolean(key: String) =
    java.lang.Boolean.parseBoolean(this.result?.get(key).toString()).nullToBoolean

// Task Result Key constants
const val switchKey = "switch"
const val changeLineKey = "changeline"
const val departPointKey = "departpoint"
const val arrivePointKey = "arrivalpoint"
fun departStationKey(i: Int) = "departstation${i}"
fun arriveStationKey(i: Int) = "arrivalstation${i}"
fun lineNameKey(i: Int) = "linename${i}"
fun lineColorKey(i: Int) = "linecolor${i}"
fun rideTimeKey(i: Int) = "ridetime${i}"
fun transportationKey(i: Int) = "transportation${if (i ==0) "e" else i}"
fun transitTimeKey(i: Int) = "transittime${if (i ==0) "e" else i}"
fun timetableKey(hour: Int) = "hour${hour.addZeroTime}"

// Current day utilities
val Int.weekDayOrEnd: String get() = when(this) { 0, 6 -> "weekend" else -> "weekday" }
val Int.weekdayString: String get() = when (this) { 0, 6 -> R.string.weekend.strings else -> R.string.weekday.strings }
val Int.weekendString: String get() = when (this) { 0, 6 -> R.string.weekday.strings else -> R.string.weekend.strings }
val Int.weekendButtonColor: Int get() = when (this) { 0, 6 -> R.color.primary else -> R.color.red }
val Int.weekdayTableColor: Int get() = when (this) { 0, 6 -> R.color.red else -> R.color.white }
val Int.otherDay: Int get() = when (this) { 0, 6 -> 1 else -> 0 }

// Convert time string to array of integers for specific hour
fun String.timeArrayInt(hour: Int): Array<Int> = if (this != "") { this
    .split(" ")
    .asSequence()
    .distinct()
    .takeWhile { it != "" }
    .map { it.toInt() }
    .takeWhile { it < 60 }
    .map { it + hour * 100 }
    .toList()
    .toTypedArray()
    .sortedArray()
} else {
    arrayOf()
}

// Convert time string to formatted string
val String.timeString: String get() = if (this != "") { this
    .split(" ")
    .distinct()
    .takeWhile { it != "" }
    .map{ it.toInt() }
    .takeWhile { it < 60 }
    .toTypedArray()
    .sortedArray()
    .joinToString(" ") { it.toString() }
} else {
    ""
}

// Add time for timetable
fun String.addTime(time: String): String = "$this $time"
    .removePrefix(" ")
    .split(" ")
    .asSequence()
    .distinct()
    .takeWhile { it != "" }
    .map { it.toInt() }
    .takeWhile { it < 60 }
    .sorted()
    .joinToString(" ") { it.toString() }

// Delete time for timetable
fun String.deleteTime(time: String): String = this
    .split(" ")
    .filter { it != time }
    .joinToString(" ") { it }

// Time sort for timetable
val String.timeSorting: String get() = this
    .replace("+","")
    .replace("-","")
    .replace("*","")
    .replace("/","")
    .replace("(","")
    .replace(")","")
    .replace(",","")
    .replace(".","")
    .replace(";","")
    .replace("N","")
    .replace("#","")
    .removePrefix(" ")
    .removePrefix(" ")
    .removePrefix(" ")
    .removePrefix(" ")
    .removePrefix(" ")
    .removePrefix(" ")
    .removePrefix(" ")
    .removePrefix(" ")
    .removePrefix(" ")
    .removePrefix(" ")
    .removeSuffix(" ")
    .removeSuffix(" ")
    .removeSuffix(" ")
    .removeSuffix(" ")
    .removeSuffix(" ")
    .removeSuffix(" ")
    .removeSuffix(" ")
    .removeSuffix(" ")
    .removeSuffix(" ")
    .removeSuffix(" ")
    .replace("  "," ")
    .replace("  "," ")
    .replace("  "," ")
    .replace("  "," ")
    .replace("  "," ")
    .replace("  "," ")
    .replace("  "," ")
    .replace("  "," ")
    .replace("  "," ")
    .replace("  "," ")
