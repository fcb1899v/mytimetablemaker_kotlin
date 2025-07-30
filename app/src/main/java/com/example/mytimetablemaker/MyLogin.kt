package com.example.mytimetablemaker
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

// Class for managing Firebase authentication
class MyLogin(
    private val context: Context,
) {

    private val myPreference = MyPreference(context)

    // Change activity with clear task flags
    private fun changeActivity(intent: Intent) {
        context.startActivity(
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    // Validation utilities
    fun isValidate(email: String, password: String, confirmPass: String, isChecked: Boolean): Boolean {
        return isValidateEmail(email) &&
               isValidatePassword(password) &&
               isValidateConfirmPass(password, confirmPass) &&
               isChecked
    }
    // Validate email format
    private fun isValidateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    // Validate password strength
    private fun isValidatePassword(password: String): Boolean {
        return Pattern.compile("^(?=.*[A-Za-z0-9])(?=.*[!@#$~]).{8,}$").matcher(password).matches()
    }
    // Validate password confirmation
    private fun isValidateConfirmPass(password: String, confirmPass: String): Boolean {
        return password == confirmPass
    }
    // Display validation error messages
    private fun validateMessage(email: String, password: String, confirmPass: String, isChecked: Boolean) {
        val isValidate = isValidate(email, password, confirmPass, isChecked)
        val message: String = when {
            !isChecked -> R.string.check_terms_and_policy.strings
            email.isBlank() -> R.string.hint_email.strings
            (!isValidateEmail(email)) -> R.string.incorrect_email_format.strings
            password.isBlank() -> R.string.hint_password.strings
            (!isValidatePassword(password)) -> R.string.incorrect_password_format.strings
            confirmPass.isBlank() -> R.string.hint_confirm_password.strings
            (!isValidateConfirmPass(password, confirmPass)) -> R.string.confirm_password_not_match.strings
            else -> R.string.input_error.strings
        }
        if (!isValidate) makeAuthToast(message)
    }

    // Sign Up functionality
    fun signup(email: String, password: String, confirmPass: String, isChecked: Boolean, progressBar: ProgressBar) {
        if (isValidate(email, password, confirmPass, isChecked)) {
            progressBar.visibility = View.VISIBLE
            Firebase.auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.INVISIBLE
                    makeAuthToast(R.string.send_auth_mail_successfully.strings)
                    changeActivity(Intent(context, LoginActivity::class.java))
                } else {
                    val message: String = when (task.exception!!) {
                        is FirebaseAuthUserCollisionException -> R.string.email_already_registered.strings
                        is FirebaseAuthWeakPasswordException -> R.string.password_over_6_characters.strings
                        else -> R.string.send_auth_mail_unsuccessfully.strings
                    }
                    progressBar.visibility = View.INVISIBLE
                    makeAuthAlert(R.string.send_auth_mail_error.strings, message)
                }
            }
        } else {
            validateMessage(email, password, confirmPass, isChecked)
        }
    }

    // Login functionality
    fun login(email: String, password: String, progressBar: ProgressBar) {
        if (isValidate(email, password, password, true)) {
            progressBar.visibility = View.VISIBLE
            Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    if (Firebase.auth.currentUser!!.isEmailVerified) {
                        // Save login state and navigate to main activity
                        myPreference.prefSaveBoolean(loginKey, true)
                        makeAuthToast(R.string.login_successfully.strings)
                        progressBar.visibility = View.INVISIBLE
                        changeActivity(Intent(context, MainActivity::class.java))
                    } else {
                        // Show email verification required message
                        progressBar.visibility = View.INVISIBLE
                        makeAuthAlert(
                            R.string.not_verified_account.strings,
                            R.string.auth_mail_check.strings
                        )
                    }
                } else {
                    // Handle login errors
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
                    progressBar.visibility = View.INVISIBLE
                    makeAuthAlert(R.string.login_error.strings, message)
                }
            }
        } else {
            validateMessage(email, password, password, true)
        }
    }

    // Logout functionality
    fun logout(progressBar: ProgressBar) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.logout.strings)
            setMessage(R.string.logout_account.strings)
            println("a")
            setPositiveButton(R.string.ok) { _, _ ->
                progressBar.visibility = View.VISIBLE
                // Sign out from Firebase and clear login state
                Firebase.auth.signOut()
                myPreference.prefSaveBoolean(loginKey,false)
                makeAuthToast(R.string.logged_out.strings)
                changeActivity(Intent(context, MainActivity::class.java))
                progressBar.visibility = View.INVISIBLE
            }
            setNegativeButton(R.string.cancel, null)
            create()
            show()
        }
    }

    // Delete Account functionality
    fun deleteAccount(progressBar: ProgressBar) {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.delete_your_account.strings)
            setMessage(R.string.delete_account)
            setNegativeButton(R.string.ok) { _, _ ->
                progressBar.visibility = View.VISIBLE
                // Delete user account from Firebase
                Firebase.auth.currentUser?.delete()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        myPreference.prefSaveBoolean(loginKey, false)
                        makeAuthToast(R.string.delete_account_successfully.strings)
                        progressBar.visibility = View.INVISIBLE
                        changeActivity(Intent(context, MainActivity::class.java))
                    } else {
                        progressBar.visibility = View.INVISIBLE
                        makeAuthAlert(R.string.delete_account_error.strings, R.string.delete_account_unsuccessfully.strings)
                    }
                }
            }
            setPositiveButton(R.string.cancel, null)
            create()
            show()
        }
    }

    // Password Reset functionality
    fun passwordReset(progressBar: ProgressBar) {
        val edittext = EditText(context)
        edittext.apply {
            setHint(R.string.hint_email)
            gravity = Gravity.CENTER
            inputType = InputType.TYPE_CLASS_TEXT
            setTextColor(Color.BLACK)
            textSize = 20F
        }
        AlertDialog.Builder(context).apply {
            setTitle(R.string.password_reset.strings)
            setMessage(R.string.reset_password.strings)
            setView(edittext)
            setPositiveButton(R.string.ok) { _, _ ->
                if (isValidateEmail(edittext.text.toString())) {
                    progressBar.visibility = View.VISIBLE
                    // Send password reset email
                    Firebase.auth.sendPasswordResetEmail(edittext.text.toString()).addOnCompleteListener { task: Task<Void> ->
                        if (task.isSuccessful) {
                            progressBar.visibility = View.INVISIBLE
                            makeAuthToast(R.string.send_auth_mail_successfully.strings)
                        } else {
                            progressBar.visibility = View.INVISIBLE
                            makeAuthAlert(R.string.password_reset_error.strings, R.string.send_reset_mail_unsuccessfully.strings)
                        }
                    }
                }
            }
            setNegativeButton(R.string.cancel, null)
            show()
        }
    }

    // Display Toast messages
    private fun makeAuthToast(message: String) {
        val ts = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        ts.setGravity(Gravity.CENTER, 0, 0)
        ts.show()
    }

    // Display Alert messages
    private fun makeAuthAlert(title: String, message: String) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.ok, null)
            show()
        }
    }
}

// Firebase Auth Error enum (commented out - for reference)
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