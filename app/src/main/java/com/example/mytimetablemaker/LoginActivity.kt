package com.example.mytimetablemaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.mytimetablemaker.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val myLogin = MyLogin(this)
        val admob = AdMob()

        val email = binding.fieldEmail.text
        val password = binding.fieldPassword.text
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.emailSignInButton.setBackgroundResource(
                    if (myLogin.isValidate(email.toString(), password.toString(), password.toString(), true))
                        R.drawable.register_button else R.drawable.not_button
                )
            }
        }

        binding.fieldEmail.addTextChangedListener(textWatcher)
        binding.fieldPassword.addTextChangedListener(textWatcher)

        //サインイン
        binding.emailSignInButton.setOnClickListener {
            myLogin.login(email.toString(), password.toString(), binding.progressBar)
        }

        //アカウント登録
        binding.emailSignUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        //パスワードリセット
        binding.resetPasswordButton.setOnClickListener {
            myLogin.passwordReset(binding.progressBar)
        }

        //AdMob
        admob.setAdMob(binding.signInAdmobView, this)
    }

    //For Back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    //For Back button
    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onSupportNavigateUp()
    }
}

