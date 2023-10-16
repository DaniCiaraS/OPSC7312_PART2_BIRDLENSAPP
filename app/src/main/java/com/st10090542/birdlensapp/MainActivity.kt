package com.st10090542.birdlensapp

import android.content.pm.PackageManager
import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the fusedLocationProviderClient and request location permissions
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkPermissionsAndRequestLocation()

        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        // Find the button and set a click listener
        val viewHotspotsButton = findViewById<Button>(R.id.viewHotspotsButton)
        viewHotspotsButton.setOnClickListener {
            // Handle the button click to fetch nearby hotspots
            fetchNearbyHotspots()
        }
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        // You can now work with the Google Map object.

        // Ensure you have location permissions and display the user's current location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            googleMap?.isMyLocationEnabled = true

            // Zoom to the user's current location
            fusedLocationProviderClient?.lastLocation
                ?.addOnSuccessListener { location ->
                    if (location != null) {
                        val userLocation = LatLng(location.latitude, location.longitude)
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 15f)
                        googleMap?.animateCamera(cameraUpdate)
                    }
                }
        }
    }


    private fun fetchNearbyHotspots() {
        // Check if you have the user's location data
        val locationTextView = findViewById<TextView>(R.id.locationTextView)
        val locationText = locationTextView.text.toString()

        if (locationText.isNotEmpty()) {
            // Parse the latitude and longitude from the text
            val (latitude, longitude) = locationText.split("\n")
                .map { it.substringAfter(":").trim().toDoubleOrNull() }

            // Check if latitude and longitude are not null
            if (latitude != null && longitude != null) {
                val backDays = 7 // Example value
                val searchRadius = 10 // Example value
                val format = "json" // Example value

                // Now you have the latitude, longitude, and other parameters
                // You can use these to make a network request to fetch nearby hotspots
                // Call your `fetchNearbyHotspots` function and pass these parameters
                // For simplicity, you can handle network requests directly here

                val apiKey = "tf6bo17rhuh9" // Replace with your API key
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val hotspotResponse = EBirdNetwork.eBirdService.getNearbyHotspots(
                            latitude,
                            longitude,
                            backDays,
                            searchRadius,
                            format,
                            apiKey
                        )

                        if (hotspotResponse.isSuccessful) {
                            val hotspots = hotspotResponse.body()?.hotspots
                            // Process the list of hotspots
                            runOnUiThread {
                                // Display the hotspots on the map or in a list
                                // You may need to update your UI here.
                            }
                        } else {
                            // Handle API errors
                            // You may need to show an error message to the user.
                        }
                    } catch (e: Exception) {
                        // Handle exceptions, e.g., network errors
                        // You may need to show an error message to the user.
                    }
                }
            }
        }
    }

    private fun checkPermissionsAndRequestLocation() {
        val hasFineLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCourseLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED && hasCourseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions, 0)
        } else {
            getLocationAndCreateUI()
        }
    }

    private fun getLocationAndCreateUI() {
        // Check if the fusedLocationProviderClient is not null
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationProviderClient?.lastLocation
                    ?.addOnSuccessListener { location ->
                        // Handle the location data
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude

                            // Now, you can use these latitude and longitude values to display the user's location on the UI.
                            // For example, set them in a TextView.
                            val locationTextView = findViewById<TextView>(R.id.locationTextView)
                            locationTextView.text = "Latitude: $latitude\nLongitude: $longitude"
                        } else {
                            // Handle the case when location is null
                            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
                        }
                    }
                    ?.addOnFailureListener { e ->
                        // Handle any errors that occur during location retrieval
                        Toast.makeText(this, "Failed to retrieve location: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: SecurityException) {
                // Handle the case when a SecurityException is thrown due to missing permissions
                Toast.makeText(this, "Location permission denied: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Request permissions from the user
            checkPermissionsAndRequestLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                getLocationAndCreateUI()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val locationRequest: LocationRequest = buildLocationRequest()
        val locationCallback: LocationCallback = buildLocationCallBack()

        // Check for permission before requesting location updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        } else {
            // If permission is not granted, request it from the user
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    private fun buildLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10.0f
        return locationRequest
    }

    private fun buildLocationCallBack(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location: Location = locationResult.lastLocation
                Log.i("LocationResult", "onLocationResult: $location")

                // Now that you have a new location, you can update your UI or perform other tasks as needed.
                // For example, update the map or recalculate routes to bird watching hotspots.
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestLocationUpdates()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient?.removeLocationUpdates(buildLocationCallBack())
    }

}
