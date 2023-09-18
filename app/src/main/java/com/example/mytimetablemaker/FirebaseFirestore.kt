package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.ktx.Firebase

class FirebaseFirestore(
    private val context: Context
) {
    //クラスの呼び出し
    private val myPreference = MyPreference(context)
    private val goOrBackArray: Array<String> = arrayOf("back1", "go1", "back2", "go2")

    //サーバーからデータ取得するダイアログの表示
    fun getAlertFirestore(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.get_saved_data.strings)
            setMessage(R.string.overwritten_current_data.strings)
            setNegativeButton(R.string.ok) { _, _ -> getFirestore(context) }
            setPositiveButton(R.string.cancel, null)
            show()
        }
    }

    //サーバーからデータ取得
    fun getFirestore(context: Context) {
        (0..3).forEach { i: Int ->
            (0..2).forEach { linenumber: Int ->
                (0..1).forEach { day: Int ->
                    (4..25).forEach { hour: Int ->
                        if (!getTimetableFireStore(context, goOrBackArray[i], linenumber, day, hour)) {
                            return
                        }
                    }
                }
            }
            if (!getLineInfoFirestore(context, goOrBackArray[i])) {
                return
            }
        }
        makeFirestoreToast(R.string.get_data_successfully.strings)
    }

    //サーバーから路線データを取得
    private fun getLineInfoFirestore(context: Context, goOrBack: String): Boolean {
        val userID: String = Firebase.auth.currentUser!!.uid
        val userDB: DocumentReference =  FirebaseFirestore.getInstance().collection("users").document(userID)
        var getFirestoreFlag = true
        val ref: DocumentReference = userDB.collection("goorback").document(goOrBack)
        ref.get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful) {
                saveLineInfoToPref(goOrBack, task)
            } else {
                makeFirestoreAlert(
                    context,
                    R.string.get_data_error.strings,
                    R.string.get_data_unsuccessfully.strings
                )
                getFirestoreFlag = false
            }
        }
        return getFirestoreFlag
    }

    //サーバーから時刻表データを取得
    private fun getTimetableFireStore(context: Context, goOrBack: String, linenumber: Int, day: Int, hour: Int): Boolean {
        val userID: String = Firebase.auth.currentUser!!.uid
        val userDB: DocumentReference =  FirebaseFirestore.getInstance().collection("users").document(userID)
        var getFirestoreFlag = true
        val ref: DocumentReference = userDB.collection("goorback").document(goOrBack)
        ref.collection("timetable").document("timetable${linenumber + 1}${day.weekDayOrEnd}").get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    getEachTimeFirestore(task, goOrBack, linenumber, day, hour)
                } else {
                    makeFirestoreAlert(
                        context,
                        R.string.get_data_error.strings,
                        R.string.get_data_unsuccessfully.strings
                    )
                    getFirestoreFlag = false
                }
            }
        return getFirestoreFlag
    }

    private fun getEachTimeFirestore(task: Task<DocumentSnapshot>, goOrBack: String, linenumber: Int, day: Int, hour: Int) {
        val key  ="${goOrBack}timetable${linenumber + 1}hour${hour.addZeroTime}${day.weekDayOrEnd}"
        myPreference.prefSaveText(key, task.taskResult("hour${hour.addZeroTime}", ""))
    }

    //サインイン時のサーバーデータの取得
    fun getFirestoreAtSignIn(context: Context) {
        val userID: String = Firebase.auth.currentUser!!.uid
        val userDB: DocumentReference =  FirebaseFirestore.getInstance().collection("users").document(userID)
        val ref: DocumentReference = userDB.collection("goorback").document("back1")
        ref.get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
               if (task.result?.get("changeline") != null) {
                   getFirestore(context)
               } else {
                   saveFirestore(context)
               }
           }
    }

    //サーバーにデータ保存するダイアログを表示
    fun saveAlertFirestore(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.save_current_data.strings)
            setMessage(R.string.overwritten_saved_data.strings)
            setNegativeButton(R.string.ok) { _, _ -> saveFirestore(context) }
            setPositiveButton(R.string.cancel, null)
            show()
        }
    }

    //サーバーにデータ保存
    private fun saveFirestore(context: Context) {
        val batch: WriteBatch = FirebaseFirestore.getInstance().batch()
        (0..3).forEach { i: Int ->
            (0..2).forEach { linenumber: Int ->
                (0..1).forEach { day: Int ->
                    saveTimetableFirestore(goOrBackArray[i], linenumber, day, batch)
                }
            }
            saveLineInfoFirestore(goOrBackArray[i], batch)
        }
        batch.commit()
            .addOnSuccessListener {
                makeFirestoreToast(R.string.save_data_successfully.strings)
            }
            .addOnFailureListener {
                makeFirestoreAlert(
                    context,
                    R.string.save_data_error.strings,
                    R.string.save_data_unsuccessfully.strings
                )
            }
    }

    //サーバーに路線データを保存
    private fun saveLineInfoFirestore(goOrBack: String, batch: WriteBatch){
        val userID: String = Firebase.auth.currentUser!!.uid
        val userDB: DocumentReference =  FirebaseFirestore.getInstance().collection("users").document(userID)
        val ref: DocumentReference = userDB.collection("goorback").document(goOrBack)
        batch.set(ref, setLineInfo(goOrBack))
    }

    //サーバーに時刻表データを保存
    private fun saveTimetableFirestore(goOrBack: String, linenumber: Int, day: Int, batch: WriteBatch){
        val userID: String = Firebase.auth.currentUser!!.uid
        val userDB: DocumentReference =  FirebaseFirestore.getInstance().collection("users").document(userID)
        val ref: DocumentReference = userDB.collection("goorback").document(goOrBack)
        val timetableRef: DocumentReference = ref.collection("timetable")
            .document("timetable${linenumber + 1}${day.weekDayOrEnd}")
        batch.set(timetableRef, setTimetableHour(goOrBack, linenumber, day))
    }

    //
    private fun saveLineInfoToPref(goOrBack: String, task: Task<DocumentSnapshot>) {
        myPreference.prefSaveBoolean("${goOrBack}switch", task.taskResultBoolean("switch"))
        myPreference.prefSaveText("${goOrBack}changeline", task.taskResult("changeline", "0"))
        myPreference.prefSaveText(goOrBack.departPointKey, task.taskResult("departpoint", goOrBack.departPointDefault))
        myPreference.prefSaveText(goOrBack.arrivePointKey, task.taskResult("arrivalpoint", goOrBack.arrivePointDefault))
        (1..3).forEach { i: Int ->
            myPreference.prefSaveText("${goOrBack}departstation${i}", task.taskResult("departstation${i}", "${R.string.depSta.strings}${i}"))
            myPreference.prefSaveText("${goOrBack}arrivalstation${i}", task.taskResult("arrivalstation${i}", "${R.string.arrSta.strings}${i}"))
            myPreference.prefSaveText("${goOrBack}linename${i}", task.taskResult("linename${i}", "${R.string.line.strings}${i}"))
            myPreference.prefSaveText("${goOrBack}linecolor${i}", task.taskResult("linecolor${i}", R.string.colorAccent.strings))
            myPreference.prefSaveText("${goOrBack}ridetime${i}", task.taskResult("ridetime${i}", ""))
            myPreference.prefSaveText("${goOrBack}transportation${i}", task.taskResult("transportation${i}", R.string.walking.strings))
            myPreference.prefSaveText("${goOrBack}transittime${i}", task.taskResult("transittime${i}", "0"))
        }
        myPreference.prefSaveText("${goOrBack}transportatione", task.taskResult("transportatione", R.string.walking.strings))
        myPreference.prefSaveText("${goOrBack}transittimee", task.taskResult("transittimee", "0"))
    }

    //
    private fun setLineInfo(goOrBack: String): LineInfo {
        val switch: Boolean = goOrBack.getRoute2Switch
        val changeLine: String = goOrBack.changeLine.toString()
        val departPoint: String = goOrBack.departPoint
        val arrivePoint: String = goOrBack.arrivePoint
        val departStation: Array<String> = goOrBack.getDepartStationFirestore
        val arriveStation: Array<String> = goOrBack.getArriveStationFirestore
        val lineName: Array<String> = goOrBack.getLineNameFirestore
        val lineColor: Array<String> = goOrBack.getLineColorFirestore
        val rideTime: Array<String> = goOrBack.getRideTimeFirestore
        val transportation: Array<String> = goOrBack.getTransportationFirestore
        val transitTime: Array<String> = goOrBack.getTransitTimeFirestore
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

    //
    private fun setTimetableHour(goOrBack: String, lineNumber: Int, day: Int): TimetableHour {
        val timetableArray: Array<String> = goOrBack.getTimetableStringArray(lineNumber, day)
        return TimetableHour(
            timetableArray[0], timetableArray[1], timetableArray[2], timetableArray[3],
            timetableArray[4], timetableArray[5], timetableArray[6], timetableArray[7],
            timetableArray[8], timetableArray[9], timetableArray[10], timetableArray[11],
            timetableArray[12], timetableArray[13], timetableArray[14], timetableArray[15],
            timetableArray[16], timetableArray[17], timetableArray[18], timetableArray[19],
            timetableArray[20], timetableArray[21]
        )
    }

    //
    fun resetPreferenceData() {
        (0..3).forEach { i: Int ->
            when(i) { 2, 3 -> myPreference.prefSaveBoolean("${goOrBackArray[i]}switch", false)}
            myPreference.prefSaveText("${goOrBackArray[i]}changeline", "0")
            myPreference.prefSaveText(goOrBackArray[i].departPointKey, goOrBackArray[i].departPointDefault)
            myPreference.prefSaveText(goOrBackArray[i].arrivePointKey, goOrBackArray[i].arrivePointDefault)
            (1..3).forEach { j: Int ->
                myPreference.prefSaveText("${goOrBackArray[i]}departstation${j}", "${R.string.depSta.strings}${j}")
                myPreference.prefSaveText("${goOrBackArray[i]}arrivalstation${j}", "${R.string.arrSta.strings}${j}")
                myPreference.prefSaveText("${goOrBackArray[i]}linename${j}", "${R.string.line.strings}${j}")
                myPreference.prefSaveText("${goOrBackArray[i]}linecolor${j}", R.string.colorAccent.strings)
                myPreference.prefSaveText("${goOrBackArray[i]}ridetime${j}", "0")
                myPreference.prefSaveText("${goOrBackArray[i]}transportation${j}", R.string.walking.strings)
                myPreference.prefSaveText("${goOrBackArray[i]}transittime${j}", "0")
            }
            myPreference.prefSaveText("${goOrBackArray[i]}transportatione", R.string.walking.strings)
            myPreference.prefSaveText("${goOrBackArray[i]}transittimee", "0")
            (0..2).forEach { linenumber: Int ->
                (0..1).forEach { day: Int ->
                    (4..25).forEach { hour: Int ->
                        myPreference.prefSaveText("${goOrBackArray[i]}timetable${linenumber + 1}hour${hour.addZeroTime}${day.weekDayOrEnd}", "")
                    }
                }
            }
        }
    }

    //
    data class LineInfo(
        val switch: Boolean = false,
        val changeLine: String = "",
        val departPoint: String = "",
        val arrivePoint: String = "",
        val departStation1: String = "",
        val departStation2: String = "",
        val departStation3: String = "",
        val arriveStation1: String = "",
        val arriveStation2: String = "",
        val arriveStation3: String = "",
        val lineName1: String = "",
        val lineName2: String = "",
        val lineName3: String = "",
        val lineColor1: String = "",
        val lineColor2: String = "",
        val lineColor3: String = "",
        val rideTime1:String = "",
        val rideTime2:String = "",
        val rideTime3:String = "",
        val transportationE: String = "",
        val transportation1: String = "",
        val transportation2: String = "",
        val transportation3: String = "",
        val transitTimeE:String = "",
        val transitTime1:String = "",
        val transitTime2:String = "",
        val transitTime3:String = ""
    )

    //
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

    //Toastの表示
    private fun makeFirestoreToast(message: String) {
        println(message)
        Log.d(ContentValues.TAG, message)
        val ts = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        ts.setGravity(Gravity.CENTER, 0, 0)
        ts.show()
    }

    //Alertの表示
    private fun makeFirestoreAlert(context: Context, title: String, message: String) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.ok, null)
            show()
        }
    }
}

