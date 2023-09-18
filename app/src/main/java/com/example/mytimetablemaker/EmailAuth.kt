package com.example.mytimetablemaker

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailAuth(
    private val context: Context,
) {
    private val myPreference = MyPreference(context)
    private val firestore = FirebaseFirestore(context)

    private fun setEditEmail(edittext: EditText) {
        edittext.apply {
            setHint(R.string.hint_email)
            gravity = Gravity.CENTER
            inputType = InputType.TYPE_CLASS_TEXT
            setTextColor(Color.BLACK)
            textSize = 20F
        }
    }

    //サインアップ入力のエラー処理
    fun signupToast(email: String, password: String, confirmPassword: String): Boolean {
        val message =  String.format("email: %s, password: %s, confirmpassword: %s", email, password, confirmPassword)
        Log.d("Signup", message)
        var toastFlag = false
        when {
            email.isBlank() -> makeAuthToast(R.string.hint_email.strings)
            password.isBlank() -> makeAuthToast(R.string.hint_password.strings)
            confirmPassword.isBlank() -> makeAuthToast(R.string.hint_confirm_password.strings)
            password != confirmPassword -> makeAuthToast(R.string.confirm_password_not_match.strings)
            else -> toastFlag = true
        }
        return toastFlag
    }

    //サインアップ時のメッセージ表示及び処理
    fun signupMessage(context: Context, task: Task<AuthResult>) {
        if (task.isSuccessful) {
            signupSuccessMessage()
        } else {
            signupErrorMessage(context, task)
        }
    }

    //サインアップエラーのメッセージ表示
    private fun signupErrorMessage(context: Context, task: Task<AuthResult>) {
        val title: String = R.string.signup_error.strings
        val message: String = when (task.exception!!) {
            is FirebaseAuthUserCollisionException -> R.string.email_already_registered.strings
            is FirebaseAuthWeakPasswordException -> R.string.password_over_6_characters.strings
            else -> ""
        }
        Log.d("Signup", String.format("%s: %s", title, message))
        makeAuthAlert(context, title, message)
    }

    //サインアップ成功時のメッセージ表示及び処理
    private fun signupSuccessMessage() {
        val message = R.string.signup_successfully.strings
        Log.d("Signup", message)
        makeAuthToast(message)
        myPreference.prefSaveBoolean("savepref", false)
        sendEmailVerification()
    }

    //認証メールの送信
    private fun sendEmailVerification() {
        val successMessage = R.string.send_auth_mail_successfully.strings
        val errorMessage = R.string.send_auth_mail_unsuccessfully.strings
        Firebase.auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task: Task<Void> ->
            if (task.isSuccessful) {
                Log.d("Signup", successMessage)
                makeAuthToast(successMessage)
            } else {
                Log.d("Signup", errorMessage)
                makeAuthAlert(context, R.string.send_auth_mail_error.strings, errorMessage)
            }
        }
    }

    //ログイン入力のエラー処理
    fun loginToast(email: String, password: String): Boolean {
        val message = String.format("email: %s, password: %s", email, password)
        Log.d("Login", message)
        var toastFlag = false
        when {
            email.isEmpty() -> makeAuthToast(R.string.hint_email.strings)
            password.isEmpty() -> makeAuthToast(R.string.hint_password.strings)
            else -> toastFlag = true
        }
        return toastFlag
    }

    //ログイン時のメッセージ表示
    fun loginMessage(context: Context, task: Task<AuthResult>): Boolean {
        return if (task.isSuccessful) {
            loginSuccessMessage(context)
        } else {
            loginErrorMessage(context, task)
        }
    }

    //初めてのログインでなければPreferencesの保存データを消去する
    private fun login() {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val isFirstLogin = pref.getBoolean("firstlogin", false)
        if (isFirstLogin) {
            Log.d("Login", "First Login")
        } else {
            Log.d("Login", "Not First Login")
            firestore.getFirestore(context)
            myPreference.prefSaveBoolean("firstlogin", true)
        }
        myPreference.prefSaveBoolean("loggedin",true)
    }

    //ログイン成功のメッセージ表示
    private fun loginSuccessMessage(context: Context): Boolean {
        val successMessage = R.string.login_successfully.strings
        val errorMessage = R.string.not_verified_account.strings
        return if (Firebase.auth.currentUser!!.isEmailVerified) {
            Log.d("Login", successMessage)
            login()
            makeAuthToast(successMessage)
            true
        } else {
            Log.d("Login", errorMessage)
            makeAuthAlert(context, errorMessage, R.string.auth_mail_check.strings)
            false
        }
    }

    //ログインエラーのメッセージ表示
    private fun loginErrorMessage(context: Context, task: Task<AuthResult>): Boolean {
        Log.d("Login", (task.exception!! as FirebaseAuthInvalidUserException).errorCode)
        val title: String = R.string.login_error.strings
        val message: String = when (task.exception!!) {
            is FirebaseAuthInvalidCredentialsException -> R.string.incorrect_email_or_password.strings
            is FirebaseAuthInvalidUserException -> {
                when ((task.exception!! as FirebaseAuthInvalidUserException).errorCode) {
                    "ERROR_INVALID_EMAIL" -> R.string.incorrect_email_format.strings
                    "ERROR_USER_NOT_FOUND" -> R.string.email_not_registered.strings
                    "ERROR_WRONG_PASSWORD" -> R.string.incorrect_email_or_password.strings
                    "ERROR_USER_DISABLED" -> R.string.account_disabled.strings
                    else -> ""
                }
            }
            else -> ""
        }
        makeAuthAlert(context, title, message)
        return false
    }

    // ログイン後の画面遷移
    fun intentToMainActivity(): Intent {
        return Intent(context, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    //ログアウト後の処理
    fun afterLogoutAccount() {
        val message = R.string.logged_out.strings
        Firebase.auth.signOut()
        Log.d("Logout", message)
        firestore.resetPreferenceData()
        myPreference.prefSaveBoolean("loggedin",false)
        makeAuthToast(message)
    }


    //ログアウト後の画面遷移
    fun intentToEmailAuthActivity(): Intent {
        return Intent(context, EmailAuthActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    //アカウント削除後の処理
    fun afterDeleteAccount() {
        val message = R.string.delete_account_successfully.strings
        Firebase.auth.signOut()
        Log.d("DeleteAccount", message)
        firestore.resetPreferenceData()
        myPreference.prefSaveBoolean("loggedin",false)
        makeAuthToast(message)
    }

    //パスワードリセットメールの送信
    private fun passwordReset(context: Context, email: String) {
        val successMessage = R.string.send_auth_mail_successfully.strings
        val errorMessage = R.string.send_reset_mail_unsuccessfully.strings
        Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener { task: Task<Void> ->
            if (task.isSuccessful) {
                Log.d("PasswordReset", successMessage)
                makeAuthToast(successMessage)
            } else {
                Log.d("PasswordReset", errorMessage)
                makeAuthAlert(context, R.string.password_reset_error.strings, errorMessage)
            }
        }
    }

    //パスワードリセット
    fun sendPasswordResetMailDialog(context: Context) {
        val edittext = EditText(context)
        setEditEmail(edittext)
        AlertDialog.Builder(context).apply {
            setTitle(R.string.password_reset.strings)
            setMessage(R.string.reset_password.strings)
            setView(edittext)
            setPositiveButton(R.string.ok) { _, _ ->
                if (edittext.text.toString().isNotEmpty()) {
                    passwordReset(context, edittext.text.toString())
                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    //Toastの表示
    private fun makeAuthToast(message: String) {
        val ts = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        ts.setGravity(Gravity.CENTER, 0, 0)
        ts.show()
    }

    //Alertの表示
    fun makeAuthAlert(context: Context, title: String, message: String) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.ok, null)
            show()
        }
    }

    //プライバシーポリシーページへの遷移
    fun intentPrivacyPolicy(): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(R.string.privacyPolicyUrl.strings)
        }
    }
}

//enum class FirebaseAuthError(val errorCode: String) {
//    USER_NOT_FOUND("ERROR_USER_NOT_FOUND"),
//    INVALID_EMAIL("ERROR_INVALID_EMAIL"),
//    WRONG_PASSWORD("ERROR_WRONG_PASSWORD"),
//    USER_DISABLED("ERROR_USER_DISABLED"),
//    INVALID_CUSTOM_TOKEN("ERROR_INVALID_CUSTOM_TOKEN"), //"The custom token format is incorrect. Please check the documentation."
//    CUSTOM_TOKEN_MISMATCH("ERROR_CUSTOM_TOKEN_MISMATCH"), //"The custom token corresponds to a different audience."));
//    INVALID_CREDENTIAL("ERROR_INVALID_CREDENTIAL"), //"The supplied auth credential is malformed or has expired."));
//    USER_MISMATCH("ERROR_USER_MISMATCH"), //"The supplied credentials do not correspond to the previously signed in user."));
//    REQUIRES_RECENT_LOGIN("ERROR_REQUIRES_RECENT_LOGIN"), //"This operation is sensitive and requires recent authentication. Log in again before retrying this request."));
//    ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL"), //"An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address."));
//    EMAIL_ALREADY_IN_USE("ERROR_EMAIL_ALREADY_IN_USE"), //"The email address is already in use by another account."));
//    CREDENTIAL_ALREADY_IN_USE("ERROR_CREDENTIAL_ALREADY_IN_USE"), //"This credential is already associated with a different user account."));
//    USER_TOKEN_EXPIRED("ERROR_USER_TOKEN_EXPIRED"), //"The user\'s credential is no longer valid. The user must sign in again."));
//    INVALID_USER_TOKEN("ERROR_INVALID_USER_TOKEN"), //"The user\'s credential is no longer valid. The user must sign in again."));
//    OPERATION_NOT_ALLOWED("ERROR_OPERATION_NOT_ALLOWED"), //"This operation is not allowed. You must enable this service in the console."));
//    WEAK_PASSWORD("ERROR_WEAK_PASSWORD"), //"The given password is invalid."));
//    MISSING_EMAIL("ERROR_MISSING_EMAIL") //"An email address must be provided.";
//}