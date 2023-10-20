package com.st10090542.birdlensapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import java.util.*
import java.util.ArrayList

class BirdObservationActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var dao: BirdObservationDao // Reference to your DAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bird_observation)

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize your DAO
        dao = BirdObservationDatabase.getDatabase(this).birdObservationDao()

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonViewObservations = findViewById<Button>(R.id.buttonViewObservations)
        val editTextSpecies = findViewById<EditText>(R.id.editTextSpecies)
        val editTextNotes = findViewById<EditText>(R.id.editTextNotes)

        buttonSave.setOnClickListener {
            val species = editTextSpecies.text.toString()
            val notes = editTextNotes.text.toString()

            // Request location permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            } else {
                lifecycleScope.launch { // Launch a coroutine in lifecycleScope
                    saveBirdObservation(species, notes)
                }
            }
        }
        buttonViewObservations.setOnClickListener {
            lifecycleScope.launch {
                val observations = dao.getAllObservations()
                val intent = Intent(this@BirdObservationActivity, ViewObservationsActivity::class.java)
                intent.putParcelableArrayListExtra("observations", ArrayList(observations))
                startActivity(intent)
            }
        }
    }

    private suspend fun saveBirdObservation(species: String, notes: String) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle permission request
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Create a BirdObservation object with the correct data types
                val observation = BirdObservation(
                    species = species,
                    notes = notes,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    date = Date()
                )

                // Call the insertObservation function within a coroutine scope
                //insertObservation(observation)
            }
        }
    }

    private suspend fun insertObservation(observation: BirdObservation) {
        // Insert observation into the database using the DAO
        dao.insertObservation(observation)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}