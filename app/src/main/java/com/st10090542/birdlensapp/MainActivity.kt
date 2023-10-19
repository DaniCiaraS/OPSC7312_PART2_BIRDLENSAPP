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
import retrofit2.Response

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
            // Specify the desired latitude and longitude
            val latitude = -33.9875
            val longitude = 18.4327 // Replace with your desired longitude

            // Call the function to fetch hotspots at the specific location
            fetchHotspotsAtLocation(latitude, longitude)
        }
    }
    private val specificCoordinates = LatLng(-33.9875, 18.4327)

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        if (specificCoordinates != null) {
            // Zoom to the specific coordinates
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(specificCoordinates, 15f)
            googleMap?.animateCamera(cameraUpdate)
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                googleMap?.isMyLocationEnabled = true

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
    }

    private fun fetchHotspotsAtLocation(latitude: Double, longitude: Double) {
        val roundedLatitude = String.format("%.2f", latitude).toDouble()
        val roundedLongitude = String.format("%.2f", longitude).toDouble()
        val backDays = 30
        val searchRadius = 50
        val apiKey = "tf6bo17rhuh9"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<List<Hotspot>> = EBirdNetwork.eBirdService.getNearbyHotspots(
                    roundedLatitude,
                    roundedLongitude,
                    backDays,
                    searchRadius,
                    "json",
                    apiKey
                )

                if (response.isSuccessful) {
                    val hotspotList: List<Hotspot>? = response.body()

                    if (hotspotList != null && hotspotList.isNotEmpty()) {
                        runOnUiThread {
                            googleMap?.clear()

                            hotspotList.forEach { hotspot ->
                                val hotspotLocation = LatLng(hotspot.lat, hotspot.lng)
                                val markerOptions = MarkerOptions()
                                    .position(hotspotLocation)
                                    .title(hotspot.locName)
                                googleMap?.addMarker(markerOptions)
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "No hotspots found in the area", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        val errorBody = response.errorBody()?.string()
                        Log.e("Hotspots", "API Error: $errorBody")
                        Toast.makeText(this@MainActivity, "API Error: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Log.e("Hotspots", "An error occurred: ${e.message}")
                    Toast.makeText(this@MainActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
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
                            locationTextView.text = "Latitude: -33.9875\nLongitude: 18.4327"

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
                val location: Location? = locationResult.lastLocation
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
