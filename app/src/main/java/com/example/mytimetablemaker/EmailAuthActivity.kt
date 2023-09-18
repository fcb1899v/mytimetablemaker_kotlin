package com.example.mytimetablemaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.mytimetablemaker.databinding.ActivityEmailauthBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailAuthActivity : AppCompatActivity() {

    //クラスの定義
    private var emailAuth = EmailAuth(Application.context)
    private var firestore = FirebaseFirestore(Application.context)
    private var admob = AdMob()
    private var pref = PreferenceManager.getDefaultSharedPreferences(Application.context)
    private var loggedIn = pref.getBoolean("loggedin", false)

    //ViewBinding
    private lateinit var binding: ActivityEmailauthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailauthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ステータスバーの設定
        window.statusBarColor = getColor(R.color.colorPrimary)

        //一度サインインしたらログイン画面をスキップ
        if (loggedIn) {
            startActivity(emailAuth.intentToMainActivity())
        }

        //サインイン
        binding.emailSignInButton.setOnClickListener {
            loginAccount(
                this,
                binding.fieldEmail.text.toString(),
                binding.fieldPassword.text.toString()
            )
        }

        //アカウント登録
        binding.emailSignUpButton.setOnClickListener {
            startActivity(Intent(this, EmailSignUpActivity::class.java))
        }

        //パスワードリセット
        binding.resetPasswordButton.setOnClickListener {
            emailAuth.sendPasswordResetMailDialog(this)
        }

        //AdMob
        admob.setAdMob(binding.signInAdmobView, this)
    }

    // ログイン処理
    private fun loginAccount(context: Context, email: String, password: String) {
        if (emailAuth.loginToast(email, password)) {
            Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->
                if (emailAuth.loginMessage(this, task)) {
                    firestore.getFirestoreAtSignIn(context)
                    startActivity(emailAuth.intentToMainActivity())
                }
            }
        }
    }
}

