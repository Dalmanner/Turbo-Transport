package com.example.turbo_transport

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.turbo_transport.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomMenu()
        db = Firebase.firestore

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        fetchAllPackages()
        binding.mapUserNameReceiverTextView.text = "Start location"

        mMap.setOnMarkerClickListener(this)

    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {

                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                    val firstPackage = db.collection("packages").document("package1")
                    firstPackage.get().addOnSuccessListener { document ->
                        val packageLocation = document.toObject(Package::class.java)
                        if (packageLocation != null) {
                            setCameraAndMap(packageLocation, mMap)
                        }
                    }
                }
            }
        }

        if (checkPermissions()) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        val packageId = marker.tag as? String ?: return false
        displayPackageInformation(packageId)
        return true
    }

    private fun displayPackageInformation(packageId: String) {
        val packageRef = db.collection("packages").document(packageId)
        packageRef.get().addOnSuccessListener { document ->
            val deliveryList = document.toObject(Package::class.java)

            binding.mapUserNameReceiverTextView.text = deliveryList?.nameOfReceiver ?: "N/A"
            binding.mapAdressTextView.text = deliveryList?.address ?: "N/A"
            binding.mapPostCodeTextView.text = deliveryList?.postCodeAddress ?: "N/A"
            binding.mapCityNameTextView.text = deliveryList?.cityName ?: "N/A"
            binding.mapKolliIdTextView.text = deliveryList?.kolliId ?: "N/A"
            if (deliveryList?.leaveAtTheDoor == true) {
                binding.mapLATDTextView.text = "LATD"
            }else
                binding.mapLATDTextView.text = ""

        }.addOnFailureListener { exception ->
            Log.e("MapsActivity", "Error fetching package details: ", exception)
        }
    }


    private fun fetchAllPackages() {

        db.collection("packages").whereEqualTo("banankaka", false).whereEqualTo("deliveryStatus", true).get().addOnSuccessListener { result ->
            for (document in result) {
                val packageLocation = document.toObject(Package::class.java)
                val lat = packageLocation.latitude
                val lng = packageLocation.longitude
                if (lat != null && lng != null) {
                    val location = LatLng(lat, lng)
                    val marker = mMap.addMarker(MarkerOptions().position(location).title("Package ID: ${document.id}"))
                    if (marker != null) {
                        marker.tag = document.id
                    }
                    setCameraAndMap(packageLocation, mMap)
                }
            }
        }.addOnFailureListener { exception ->
            Log.d("MapsActivity", "Error fetching packages: ", exception)
        }
    }
    private fun setMarkersAndRoute(startLatLng: LatLng, endLatLng: LatLng) {
        mMap.addMarker(MarkerOptions().position(startLatLng).title("Start"))
        endLatLng?.let { MarkerOptions().position(it).title("End") }
            ?.let { mMap.addMarker(it) }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startLatLng))

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(startLatLng, 11.5f)
        mMap.moveCamera(cameraUpdate)
        fetchDirections(startLatLng, endLatLng, mMap)
    }

    @SuppressLint("MissingPermission")
    private fun setCameraAndMap(thisPackage: Package, mMap: GoogleMap) {
        val endLatLng = thisPackage.latitude?.let {
            thisPackage.longitude?.let { it1 ->
                LatLng(
                    it,
                    it1
                )
            }
        }

        val currentUserLocation =
            LocationServices.getFusedLocationProviderClient(this)
        currentUserLocation.lastLocation.addOnSuccessListener { location ->

            if (location != null) {
                val startLatLng = LatLng(location.latitude, location.longitude)
                endLatLng?.let { setMarkersAndRoute(startLatLng, it) }
            }
        }
        mMap.isMyLocationEnabled = true
    }


    private fun fetchDirections(
        startLatLng: LatLng,
        endLatLng: LatLng,
        googleMap: GoogleMap
    ) {
        val apiKey = BuildConfig.API_KEY
        val client = OkHttpClient()
        val request = Request.Builder()

        .url("https://maps.googleapis.com/maps/api/directions/json?origin=${startLatLng.latitude},${startLatLng.longitude}&destination=${endLatLng.latitude},${endLatLng.longitude}&mode=driving&key=$apiKey")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!it.isSuccessful) throw IOException("Unexpected code $it")

                    val responseData = it.body?.string()
                    val gson = Gson()
                    val directionsResult =
                        gson.fromJson(responseData, DirectionsResult::class.java)

                    if (directionsResult.routes.isNotEmpty() && directionsResult.routes[0].legs.isNotEmpty()) {
                        val steps = directionsResult.routes[0].legs[0].steps

                        runOnUiThread {
                            val polylineOptions = PolylineOptions().width(10f)
                                .color(Color.BLUE) //Custom design of route
                            steps.forEach { step ->
                                val decodedPath =
                                    decodePolyline(step.polyline.points) //decode each line to latlng
                                polylineOptions.addAll(decodedPath)
                            }
                            googleMap.addPolyline(polylineOptions)
                        }
                    } else {
                        Log.d("MapsActivity", "No routes found")
                    }
                }
            }
        })
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RouteActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {

            }
        }
    }

    private fun bottomMenu() {
        val bottomNavigation = findViewById<NavigationBarView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    val intent = Intent(this, ListDeliveries::class.java)
                    startActivity(intent)
                    true
                }
                R.id.item_2 -> {

                    true
                }
                R.id.item_3 -> {
                    val intent = Intent(this, AddPackageActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        bottomNavigation.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    val intent = Intent(this, ListDeliveries::class.java)
                    startActivity(intent)
                    true
                }
                R.id.item_2 -> {

                    true
                }
                R.id.item_3 -> {
                    val intent = Intent(this, AddPackageActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}