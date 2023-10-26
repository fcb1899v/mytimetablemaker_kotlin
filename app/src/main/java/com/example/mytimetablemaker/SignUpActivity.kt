package com.example.mytimetablemaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.mytimetablemaker.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val myLogin = MyLogin(this)
        val admob = AdMob()

        val email = binding.fieldEmail.text
        val password = binding.fieldPassword.text
        val confirmPass = binding.fieldConfirmPassword.text
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.signUpRegisterButton.setBackgroundResource(
                    if (myLogin.isValidate(email.toString(), password.toString(), confirmPass.toString(), binding.signUpCheckBox.isChecked))
                        R.drawable.register_button else R.drawable.not_button
                )
            }
        }

        binding.fieldEmail.addTextChangedListener(textWatcher)
        binding.fieldPassword.addTextChangedListener(textWatcher)
        binding.fieldConfirmPassword.addTextChangedListener(textWatcher)

        //アカウント登録
        binding.signUpRegisterButton.setOnClickListener {
            myLogin.signup(
                email.toString(),
                password.toString(),
                confirmPass.toString(),
                binding.signUpCheckBox.isChecked,
                binding.progressBar
            )
        }

        //利用規約・プライバシーポリシー
        binding.signUpPrivacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(R.string.privacyPolicyUrl.strings)
            })
        }

        //利用規約・プライバシーポリシーのチェックボックスで登録ボタン解除
        binding.signUpCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val isValidate = myLogin.isValidate(email.toString(), password.toString(), confirmPass.toString(), isChecked)
            binding.signUpRegisterButton.setBackgroundResource(
                if (isValidate) R.drawable.register_button else R.drawable.not_button
            )
        }

        //AdMob
        admob.setAdMob(binding.signUpAdmobView, this)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}

