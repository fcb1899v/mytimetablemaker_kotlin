package com.example.mytimetablemaker

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.mytimetablemaker.databinding.ActivitySettingsBinding

// Activity for managing app settings
class SettingsActivity: AppCompatActivity() {

    // ViewBinding for accessing views
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewBinding
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Load settings fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commitAllowingStateLoss()
    }

    // Handle back button press in action bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    // Handle back button press for navigation
    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onSupportNavigateUp()
    }
}