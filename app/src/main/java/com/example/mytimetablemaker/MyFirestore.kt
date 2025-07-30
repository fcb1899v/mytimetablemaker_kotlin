package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.ktx.Firebase

// Class for managing Firestore database operations
class MyFirestore(
    private val context: Context
) {

    private val myPreference = MyPreference(context)
    private var isSuccessFirestore = false

    // Get Firestore data
    fun getFirestore(progressBar: ProgressBar) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.get_saved_data.strings)
            setMessage(R.string.overwritten_current_data.strings)
            setPositiveButton(R.string.ok) { _, _ ->
                progressBar.visibility = View.VISIBLE
                isSuccessFirestore = true
                // Iterate through all routes and data
                (0..3).forEach { i: Int ->
                    if (isSuccessFirestore) getLineInfoFirestore(goOrBackArray[i])
                    (0..2).forEach { linenumber: Int ->
                        (0..1).forEach { day: Int ->
                            (4..25).forEach { hour: Int ->
                                if (isSuccessFirestore) getTimetableFireStore(goOrBackArray[i], linenumber, day, hour)
                                if (i == 3 && linenumber == 2 && day == 1 && hour == 25) {
                                    if (isSuccessFirestore) {
                                        makeFirestoreToast(R.string.get_data_successfully.strings)
                                        context.startActivity(Intent(context, MainActivity::class.java)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        )
                                    }
                                    progressBar.visibility = View.INVISIBLE
                                }
                            }
                        }
                    }
                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    // Get Line information data from Firestore
    private fun getLineInfoFirestore(goOrBack: String) {
        val userID: String = Firebase.auth.currentUser!!.uid
        val userDB: DocumentReference =  FirebaseFirestore.getInstance().collection(usersColPath).document(userID)
        val ref: DocumentReference = userDB.collection(goOrBackDocPath).document(goOrBack)
        ref.get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
            isSuccessFirestore = if (task.isSuccessful) {
                saveLineInfoToPref(goOrBack, task)
                true
            } else {
                makeFirestoreAlert(R.string.get_data_error.strings, R.string.get_data_unsuccessfully.strings)
                false
            }
        }
    }

    // Get timetable data from Firestore
    private fun getTimetableFireStore(goOrBack: String, linenumber: Int, day: Int, hour: Int) {
        val userID: String = Firebase.auth.currentUser!!.uid
        val userDB: DocumentReference =  FirebaseFirestore.getInstance().collection(usersColPath).document(userID)
        val ref: DocumentReference = userDB.collection(goOrBackDocPath).document(goOrBack)
        ref.collection(timetableColPath).document(timetableDocPath(linenumber, day)).get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                isSuccessFirestore = if (task.isSuccessful) {
                    myPreference.prefSaveText(goOrBack.timetableKey(linenumber, hour, day), task.taskResult(timetableKey(hour), ""))
                    true
                } else {
                    makeFirestoreAlert(R.string.get_data_error.strings, R.string.get_data_unsuccessfully.strings)
                    false
                }
            }
    }

    // Save Firestore data
    fun setFirestore(progressBar: ProgressBar) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.save_current_data.strings)
            setMessage(R.string.overwritten_saved_data.strings)
            setNegativeButton(R.string.ok) { _, _ ->
                progressBar.visibility = View.VISIBLE
                val batch: WriteBatch = FirebaseFirestore.getInstance().batch()
                val userID: String = Firebase.auth.currentUser!!.uid
                val userDB: DocumentReference =  FirebaseFirestore.getInstance().collection(usersColPath).document(userID)
                // Iterate through all routes and save data
                (0..3).forEach { i: Int ->
                    val ref: DocumentReference = userDB.collection(goOrBackDocPath).document(goOrBackArray[i])
                    batch.set(ref, setLineInfo(goOrBackArray[i]))
                    (0..2).forEach { linenumber: Int ->
                        (0..1).forEach { day: Int ->
                            val timetableRef: DocumentReference = ref.collection(timetableColPath).document(timetableDocPath(linenumber, day))
                            batch.set(timetableRef, setTimetableHour(goOrBackArray[i], linenumber, day))
                        }
                    }
                }
                batch.commit().addOnSuccessListener {
                    makeFirestoreToast(R.string.save_data_successfully.strings)
                    context.startActivity(Intent(context, MainActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                    progressBar.visibility = View.INVISIBLE
                }.addOnFailureListener {
                    makeFirestoreAlert(R.string.save_data_error.strings, R.string.save_data_unsuccessfully.strings)
                    progressBar.visibility = View.INVISIBLE
                }
            }
            setPositiveButton(R.string.cancel, null)
            show()
        }
    }

    // Save line information from Firestore to SharedPreferences
    private fun saveLineInfoToPref(goOrBack: String, task: Task<DocumentSnapshot>) {
        myPreference.prefSaveBoolean(goOrBack.route2Key, task.taskResultBoolean(switchKey))
        myPreference.prefSaveText(goOrBack.changeLineKey, task.taskResult(changeLineKey, changeLineDefault))
        myPreference.prefSaveText(goOrBack.departPointKey, task.taskResult(departPointKey, goOrBack.departPointDefault))
        myPreference.prefSaveText(goOrBack.arrivePointKey, task.taskResult(arrivePointKey, goOrBack.arrivePointDefault))
        // Save station and line information for each transfer
        (1..3).forEach { i: Int ->
            myPreference.prefSaveText(goOrBack.departStationKey(i), task.taskResult(departStationKey(i), departStationDefault(i)))
            myPreference.prefSaveText(goOrBack.arriveStationKey(i), task.taskResult(arriveStationKey(i), arriveStationDefault(i)))
            myPreference.prefSaveText(goOrBack.lineNameKey(i), task.taskResult(lineNameKey(i), lineNameDefault(i)))
            myPreference.prefSaveText(goOrBack.lineColorKey(i), task.taskResult(lineColorKey(i), lineColorDefault.toString()))
            myPreference.prefSaveText(goOrBack.rideTimeKey(i), task.taskResult(rideTimeKey(i), rideTimeDefault))
            myPreference.prefSaveText(goOrBack.transportationKey(i), task.taskResult(transportationKey(i), transportationDefault))
            myPreference.prefSaveText(goOrBack.transitTimeKey(i), task.taskResult(transitTimeKey(i), transitTimeDefault))
        }
        // Save transportation and transit time for initial segment
        myPreference.prefSaveText(goOrBack.transportationKey(0), task.taskResult(transportationKey(0), transportationDefault))
        myPreference.prefSaveText(goOrBack.transitTimeKey(0), task.taskResult(transitTimeKey(0), transitTimeDefault))
    }

    // Create LineInfo data class from SharedPreferences data
    private fun setLineInfo(goOrBack: String): LineInfo {
        val switch: Boolean = myPreference.getRoute2Switch(goOrBack)
        val changeLine: String = myPreference.changeLine(goOrBack).toString()
        val departPoint: String = myPreference.departPoint(goOrBack)
        val arrivePoint: String = myPreference.arrivePoint(goOrBack)
        val departStation: Array<String> = myPreference.departStationFirestore(goOrBack)
        val arriveStation: Array<String> = myPreference.arriveStationFirestore(goOrBack)
        val lineName: Array<String> = myPreference.lineNameFirestore(goOrBack)
        val lineColor: Array<String> = myPreference.lineColorFirestore(goOrBack)
        val rideTime: Array<String> = myPreference.rideTimeFirestore(goOrBack)
        val transportation: Array<String> = myPreference.transportationFirestore(goOrBack)
        val transitTime: Array<String> = myPreference.transitTimeFirestore(goOrBack)
        return LineInfo(switch, changeLine, departPoint, arrivePoint,
            departStation[0], departStation[1], departStation[2],
            arriveStation[0], arriveStation[1], arriveStation[2],
            lineName[0], lineName[1], lineName[2],
            lineColor[0], lineColor[1], lineColor[2],
            rideTime[0], rideTime[1], rideTime[2],
            transportation[0], transportation[1], transportation[2], transportation[3],
            transitTime[0], transitTime[1], transitTime[2], transitTime[3]
        )
    }

    // Create TimetableHour data class from SharedPreferences data
    private fun setTimetableHour(goOrBack: String, lineNumber: Int, day: Int): TimetableHour {
        val timetableArray: Array<String> = myPreference.getTimetableStringArray(goOrBack, lineNumber, day)
        return TimetableHour(
            timetableArray[0], timetableArray[1], timetableArray[2], timetableArray[3],
            timetableArray[4], timetableArray[5], timetableArray[6], timetableArray[7],
            timetableArray[8], timetableArray[9], timetableArray[10], timetableArray[11],
            timetableArray[12], timetableArray[13], timetableArray[14], timetableArray[15],
            timetableArray[16], timetableArray[17], timetableArray[18], timetableArray[19],
            timetableArray[20], timetableArray[21]
        )
    }

    // Data class for storing line information in Firestore
    data class LineInfo(
        val switch: Boolean = false,
        val changeline: String = "",
        val departpoint: String = "",
        val arrivalpoint: String = "",
        val departstation1: String = "",
        val departstation2: String = "",
        val departstation3: String = "",
        val arrivalstation1: String = "",
        val arrivalstation2: String = "",
        val arrivalstation3: String = "",
        val linename1: String = "",
        val linename2: String = "",
        val linename3: String = "",
        val linecolor1: String = "",
        val linecolor2: String = "",
        val linecolor3: String = "",
        val ridetime1:String = "",
        val ridetime2:String = "",
        val ridetime3:String = "",
        val transportatione: String = "",
        val transportation1: String = "",
        val transportation2: String = "",
        val transportation3: String = "",
        val transittimee:String = "",
        val transittime1:String = "",
        val transittime2:String = "",
        val transittime3:String = ""
    )

    // Data class for storing timetable hour data in Firestore
    data class TimetableHour(
        val hour04: String = "",
        val hour05: String = "",
        val hour06: String = "",
        val hour07: String = "",
        val hour08: String = "",
        val hour09: String = "",
        val hour10: String = "",
        val hour11: String = "",
        val hour12: String = "",
        val hour13: String = "",
        val hour14: String = "",
        val hour15: String = "",
        val hour16: String = "",
        val hour17: String = "",
        val hour18: String = "",
        val hour19: String = "",
        val hour20: String = "",
        val hour21: String = "",
        val hour22: String = "",
        val hour23: String = "",
        val hour24: String = "",
        val hour25: String = ""
    )

    // Display Toast
    private fun makeFirestoreToast(message: String) {
        println(message)
        Log.d(ContentValues.TAG, message)
        val ts = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        ts.setGravity(Gravity.CENTER, 0, 0)
        ts.show()
    }

    // Display Alert
    private fun makeFirestoreAlert(title: String, message: String) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.ok, null)
            show()
        }
    }
}

