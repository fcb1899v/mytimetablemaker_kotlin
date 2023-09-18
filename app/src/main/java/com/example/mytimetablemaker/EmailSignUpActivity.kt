package com.example.mytimetablemaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.mytimetablemaker.Application.Companion.context
import com.example.mytimetablemaker.databinding.ActivityEmailsignupBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailSignUpActivity : AppCompatActivity() {

    //クラスの定義
    private var emailAuth = EmailAuth(context)
    private val admob = AdMob()

    //ViewBinding
    private lateinit var binding: ActivityEmailsignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailsignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = binding.fieldEmail.text
        val password = binding.fieldPassword.text
        val confirmPass = binding.fieldConfirmPassword.text

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isValidChecked = isValid(email.toString(), password.toString(), confirmPass.toString()) && binding.signUpCheckBox.isChecked
                binding.signUpRegisterButton.setBackgroundResource(
                    if (isValidChecked) R.drawable.register_button else R.drawable.not_button
                )
            }
        }

        binding.fieldEmail.addTextChangedListener(textWatcher)
        binding.fieldPassword.addTextChangedListener(textWatcher)
        binding.fieldConfirmPassword.addTextChangedListener(textWatcher)

        //アカウント登録
        binding.signUpRegisterButton.setOnClickListener {
            val isValidChecked = isValid(email.toString(), password.toString(), confirmPass.toString()) && binding.signUpCheckBox.isChecked
            if (isValidChecked) { signupAccount(context, email.toString(), password.toString(), confirmPass.toString()) }
        }

        //利用規約・プライバシーポリシー
        binding.signUpPrivacyPolicy.setOnClickListener {
            startActivity(emailAuth.intentPrivacyPolicy())
        }

        //利用規約・プライバシーポリシーのチェックボックスで登録ボタン解除
        binding.signUpCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val isValidChecked = isValid(email.toString(), password.toString(), confirmPass.toString()) && isChecked
            binding.signUpRegisterButton.setBackgroundResource(
                if (isValidChecked) R.drawable.register_button else R.drawable.not_button
            )
        }

        //AdMob
        admob.setAdMob(binding.signUpAdmobView, this)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    //バリデーションロジック
    private fun isValid(email: String, password: String, confirmPass: String): Boolean {
        val isValidEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isValidPassword = password.length >= 8
        val isValidConfirmPass = password == confirmPass
        return isValidEmail && isValidPassword && isValidConfirmPass
    }

    // ユーザー登録処理
    private fun signupAccount(context: Context, email: String, password: String, confirmPass: String) {
        if (emailAuth.signupToast(email, password, confirmPass)) {
            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->
                emailAuth.signupMessage(context, task)
                finish()
            }
        }
    }
}

