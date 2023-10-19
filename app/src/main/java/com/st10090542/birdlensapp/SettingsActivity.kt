package com.st10090542.birdlensapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val unitSelectionGroup = findViewById<RadioGroup>(R.id.unitSelectionGroup)
        val metricSwitch = findViewById<Switch>(R.id.metricSwitch)
        val maxDistanceEditText = findViewById<EditText>(R.id.maxDistanceEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)

        // Load existing preferences
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val useMetricSystem = sharedPreferences.getBoolean("useMetricSystem", true)
        val metricRadioButton = findViewById<RadioButton>(R.id.metricRadioButton)
        val imperialRadioButton = findViewById<RadioButton>(R.id.imperialRadioButton)
        metricRadioButton.isChecked = useMetricSystem
        imperialRadioButton.isChecked = !useMetricSystem

        saveButton.setOnClickListener {
            // Save unit preference
            val editor = sharedPreferences.edit()
            val selectedRadioButton = findViewById<RadioButton>(unitSelectionGroup.checkedRadioButtonId)
            val useMetricSystem = selectedRadioButton.id == R.id.metricRadioButton

            // Update the setting
            editor.putBoolean("useMetricSystem", useMetricSystem)
            editor.apply()

            // Create an Intent to navigate to MainActivity
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(intent)

            // Perform unit conversion here if needed
            if (useMetricSystem) {
                // Convert to metric units
                val distanceInKilometers = 100.0 // Replace with your actual distance
                val distanceInMiles = kilometersToMiles(distanceInKilometers)
                Toast.makeText(this, "Distance in Miles: $distanceInMiles", Toast.LENGTH_SHORT).show()
            } else {
                // Convert to imperial units
                val distanceInMiles = 62.1371 // Replace with your actual distance in miles
                val distanceInKilometers = milesToKilometers(distanceInMiles)
                Toast.makeText(this, "Distance in Kilometers: $distanceInKilometers", Toast.LENGTH_SHORT).show()
            }

            Toast.makeText(this, "Preferences saved", Toast.LENGTH_SHORT).show()

        }
    }

    // Conversion functions
    private fun kilometersToMiles(kilometers: Double): Double {
        return kilometers * 0.621371
    }

    private fun milesToKilometers(miles: Double): Double {
        return miles / 0.621371
    }



}