package com.example.ninesolpractise

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ninesolpractise.SharedFunctions.SharedFunctions.addValuesToSharedPreferences
import com.example.ninesolpractise.SharedFunctions.SharedFunctions.getValuesFromSharedPreferences
import com.example.ninesolpractise.SharedFunctions.SharedFunctions.initializeSharedPreferences
import com.example.ninesolpractise.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeSharedPreferences(this)

        binding.workDurationEditText.hint =
            getValuesFromSharedPreferences("workDuration").toString()
        binding.breakDurationEditText.hint = getValuesFromSharedPreferences("breakDuration").toString()

        binding.saveSettingsButton.setOnClickListener {
            saveSettings()
        }

    }

    private fun saveSettings() {
        val workDuration = binding.workDurationEditText.text.toString().toIntOrNull() ?: 25
        val breakDuration = binding.breakDurationEditText.text.toString().toIntOrNull() ?: 5

        addValuesToSharedPreferences("workDuration", workDuration)
        addValuesToSharedPreferences("breakDuration", breakDuration)

        finish()
    }

}