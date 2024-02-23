package com.example.turbo_transport

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.turbo_transport.BuildConfig.API_KEY
import com.example.turbo_transport.BuildConfig.SERVER_KEY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.turbo_transport.databinding.ActivityRouteBinding
import com.google.android.gms.cloudmessaging.CloudMessage
//import com.google.android.gms.common.api.Response
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.IOException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.Date
import java.util.logging.Handler


class RouteActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101
        const val notificationId = 1
    }

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var cloudMessage: CloudMessage

    private lateinit var topAdressTextView: TextView
    private lateinit var postCodeTextView: TextView
    private lateinit var travelTimeTextView: TextView
    private lateinit var kmLeftTextView: TextView

    private lateinit var appBarLayout: AppBarLayout
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var notDeliveredButton: Button
    private lateinit var continueDeliverButton: Button
    private lateinit var driveMapButton: Button

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityRouteBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var mapProgressBar: ProgressBar
    private lateinit var documentId: String

    private var barcode = ""

    private var start = LatLng(57.1, 18.1)
    private var end = LatLng(57.0, 18.0)
    private var currentPolyline: com.google.android.gms.maps.model.Polyline? = null
    private var driverMode = false
    private var firstRun = true
    private var lastUpdatedLocation: LatLng? = null

    private var lastTimestamp: Timestamp? = null
    private var currentTimestamp: Timestamp? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Setup firebase variables
        db = Firebase.firestore
        auth = Firebase.auth
        storage = Firebase.storage
        documentId = intent.getStringExtra("documentId").toString()

        binding = ActivityRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViews()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        getPackageInformation(documentId)
        showMenu()

        continueDeliverButton.setOnClickListener {
            sendToBarCodeReader(barcode)
            stopLocationUpdates()
        }
        notDeliveredButton.setOnClickListener {
            stopLocationUpdates()
            sendToEnd()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //When map is ready, get package
        getPackage(documentId, mMap)

        android.os.Handler(Looper.getMainLooper()).postDelayed({
            mapProgressBar.visibility = View.GONE
        }, 1000)
    }

    //Function to get new location updates so we can focus and follow with the camera map.
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    //Update location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))

                    //Get new times
                    fetchDirections(currentLatLng, end, mMap)

                }
            }
        }

        if (checkPermissions()) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
            )
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            stopLocationUpdates()
        } catch (e: UninitializedPropertyAccessException) {
            Log.d("LocationCallback", "locationCallback is not initialized.")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            stopLocationUpdates()
        } catch (e: UninitializedPropertyAccessException) {
            Log.d("LocationCallback", "locationCallback is not initialized.")
        }
    }

    override fun onResume() {
        super.onResume()
//        startLocationUpdates()
    }


    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun getPackage(documentId: String, mMap: GoogleMap) {
        db.collection("packages").document(documentId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("!!!", "Listen failed.", e)
                return@addSnapshotListener
            }
            val thisPackage = snapshot?.toObject(Package::class.java)

            if (snapshot != null && snapshot.exists()) {
                if (thisPackage != null) {
                    if (checkLatLong(thisPackage.latitude, thisPackage.longitude)) {
                        //If coordinates are OK, go ahead and create map with markers and route
                        if (!driverMode){
                            setCameraAndMap(thisPackage, mMap)
//                            lastTimestamp = Timestamp(Date()) // Update current timestamp to now
                        }
                    }
                } else {

                    //Create toast in case of error
                    Toast.makeText(
                        this,
                        "Error loading route, something wrong with coordinates, check with admin",
                        Toast.LENGTH_LONG
                    ).show()

                    //Go back to previous activity, perhaps suggest open google maps app?
                    finish()
                }
            }
        }
    }

    private fun setMarkersAndRoute(startLatLng: LatLng, endLatLng: LatLng) {
        mMap.addMarker(MarkerOptions().position(startLatLng).title("Start"))
        endLatLng?.let { MarkerOptions().position(it).title("End") }
            ?.let {
                mMap.addMarker(it)
                end = endLatLng
            }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startLatLng))

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(startLatLng, 10f)
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

        //Get user current position
        val currentUserLocation =
            LocationServices.getFusedLocationProviderClient(this)
        currentUserLocation.lastLocation.addOnSuccessListener { location ->

            //Check to see if it is not null, then get coordinates
            if (location != null) {
                val startLatLng = LatLng(location.latitude, location.longitude)
                endLatLng?.let { setMarkersAndRoute(startLatLng, it) }
            }
        }

        //Show position on map
        mMap.isMyLocationEnabled = true

        //Run frequent updates when driver mode is selected
        driveMapButton.setOnClickListener {
            startLocationUpdates()
            driverMode = true
        }
    }
    private fun sendNotificationToUser() {
        val client = OkHttpClient()
        getUserToken { userToken ->
            Log.d("!!!", "number1$userToken")
            getDeliveryTime { readableDate ->
                getSenderName { senderName ->
                    val json = Gson().toJson(
                        mapOf(
                            "to" to userToken,
                            "notification" to mapOf(
                                "title" to "TurboTransport",
                                "body" to "Paket från $senderName levereras idag $readableDate"
                            )
                        )
                    )

                    val body =
                        RequestBody.create(
                            "application/json; charset=utf-8".toMediaTypeOrNull(),
                            json
                        )

                    val request = Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "key=${SERVER_KEY}")
                        .post(body)
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            // Hantera fel här
                            Log.e("NotificationError", e.message ?: "Unknown error")
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (!response.isSuccessful) {
                                Log.e(
                                    "NotificationResponse",
                                    "Failed to send notification: ${response.body?.string()}"
                                )
                            }
                        }
                    })
                }
            }
        }
    }
    private fun getUserId(callback: (String) -> Unit) {
        val user = auth.currentUser
        var userId = ""
        if (user != null) {
            db.collection("packages").document(documentId)
                .get().addOnSuccessListener { document ->
                    if (document != null) {
                        val userIdReceiver = document.get("userIdReceiver")
                        if (userIdReceiver != null) {
                            userId = userIdReceiver.toString()
                            callback(userId)
                        }
                    } else {
                        return@addOnSuccessListener
                    }
                }
        }
    }
    fun getUserToken(callback: (String) -> Unit) {
        val user = auth.currentUser
        var userToken = ""
        getUserId { userId ->
            if (user != null) {
                db.collection("users").document(userId)
                    .get().addOnSuccessListener { document ->
                        if (document != null) {
                            val notificationToken = document.get("notificationToken")
                            if (notificationToken != null) {
                                userToken= notificationToken.toString()
                                callback(userToken)
                            }
                        } else {
                            return@addOnSuccessListener
                        }
                    }
            }
        }

    }
        fun getSenderName(callback: (String) -> Unit) {
            val user = auth.currentUser

            if (user != null) {
                db.collection("packages").document(documentId)
                    .get().addOnSuccessListener { document ->
                        if (document != null) {
                            val dbSenderName = document.get("senderName")
                            if (dbSenderName != null) {
                                val senderName = dbSenderName.toString()
                                callback(senderName)
                            }
                        }
                    }
            }
        }
    fun getDeliveryTime(callback: (String) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            db.collection("packages").document(documentId)
                .get().addOnSuccessListener { document ->
                    if (document != null) {
                        val dbExpectedDelieryTime =
                            document.get("expectedDeliveryTime") as com.google.firebase.Timestamp
                        val date = dbExpectedDelieryTime.toDate()
                        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                        val readableDate = formatter.format(date)
                        callback(readableDate)
                    }
                }
        }
    }

        //Function to check value of coordinates
    private fun checkLatLong(latStr: Double?, longStr: Double?): Boolean {

        try {
            val lat = latStr
            val long = longStr

            if (lat == null || long == null) {
                return false
            }

            if (lat !in -90.0..90.0 || long !in -180.0..180.0) {
                return false
            }
            return true
        } catch (e: NumberFormatException) {
            Log.d("!!!", "error with lat lng")
        }
        return false
    }

    //Get the directions
    private fun fetchDirections(startLatLng: LatLng, endLatLng: LatLng, googleMap: GoogleMap) {
        val apiKey = BuildConfig.API_KEY
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://maps.googleapis.com/maps/api/directions/json?origin=${startLatLng.latitude},${startLatLng.longitude}&destination=${endLatLng.latitude},${endLatLng.longitude}&mode=driving&key=$apiKey")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Error fetching directions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "Failed to fetch directions",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return
                    }

                    val responseData = it.body?.string()
                    val gson = Gson()
                    val directionsResult = gson.fromJson(responseData, DirectionsResult::class.java)

                    if (directionsResult.routes.isNotEmpty() && directionsResult.routes[0].legs.isNotEmpty()) {

                        handleDirectionResults(directionsResult, googleMap)

                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "No routes found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }

    private fun handleDirectionResults(directionsResult: DirectionsResult, googleMap: GoogleMap) {

        var totalDistanceMeters = 0 //dist
        var totalDurationSeconds = 0 //time
        val route =
            directionsResult.routes[0] //First route
        for (leg in route.legs) {
            totalDistanceMeters += leg.distance.value
            totalDurationSeconds += leg.duration.value
        }

        //Convert distance and time to string
        val totalDistanceKm =
            totalDistanceMeters / 1000.0 //Convert to km
        val totalDistanceText = String.format("%.2f km", totalDistanceKm)

        val totalHours = totalDurationSeconds / 3600
        val totalMinutes = (totalDurationSeconds % 3600) / 60
        val totalDurationText = "$totalHours h $totalMinutes min"

        //Update database with delivery time
        checkAndCalculateETA(totalDurationSeconds)

        runOnUiThread {
            //Remove previous polyline before creating new one.
            currentPolyline?.remove()

            val polylineOptions = PolylineOptions().width(10f).color(Color.BLUE)
            val steps =
                directionsResult.routes[0].legs[0].steps
            steps.forEach { step ->
                val decodedPath = decodePolyline(step.polyline.points)
                polylineOptions.addAll(decodedPath)
            }
            //Save new polyline
            currentPolyline = googleMap.addPolyline(polylineOptions)

            //Update views
            travelTimeTextView.text = totalDurationText
            kmLeftTextView.text = totalDistanceText
        }
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
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
    private fun checkAndCalculateETA(totalDurationInSeconds: Int) {
        currentTimestamp = Timestamp(Date()) // Update current timestamp to now
        // Ensure both timestamps are not null
        if (lastTimestamp != null && currentTimestamp != null) {
            // Convert timestamps to milliseconds and calculate the difference
            val difference = currentTimestamp!!.toDate().time - lastTimestamp!!.toDate().time
            // If more than 5 minutes apart, execute your code
            if (difference > 1 * 60 * 1000) { // 1 minutes in milliseconds
                calculateAndUpdateETA(totalDurationInSeconds)
                // Update lastTimestamp to currentTimestamp after checking
                lastTimestamp = currentTimestamp
            }
        }
        else if (firstRun && driverMode){
            calculateAndUpdateETA(totalDurationInSeconds)
            lastTimestamp = currentTimestamp
            firstRun = false
            sendNotificationToUser()
        }
    }
    private fun calculateAndUpdateETA(totalDurationInSeconds: Int) {

        //Calculate new ETA
        val currentTime = System.currentTimeMillis()
        val etaMillis = currentTime + totalDurationInSeconds * 1000 //Convert to milliseconds
        val newETA = com.google.firebase.Timestamp(Date(etaMillis))

        updateFirestoreTimestamp(newETA)
    }

    private fun updateFirestoreTimestamp(newETA: com.google.firebase.Timestamp) {

        val collectionPath = "packages"

        //Update database with new ETA
        db.collection(collectionPath).document(documentId)
            .update("expectedDeliveryTime", newETA)
            .addOnSuccessListener {
                Log.d("!!!", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("!!!", "Error updating document", e)
            }
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
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            }
        }
    }

    private fun sendToBarCodeReader(packageBarcode: String) {

        val intent = Intent(this, BarCodeReaderActivity::class.java)
        intent.putExtra("barcodeValue", packageBarcode)
        intent.putExtra("documentId", documentId)
        startActivity(intent)
    }
    private fun sendToEnd() {

        val intent = Intent(this, PackageDeliveredActivity::class.java)
        intent.putExtra("failedDelivery", true)
        intent.putExtra("documentId", documentId)
        startActivity(intent)
    }

    private fun getPackageInformation(documentId: String) {
        db.collection("packages").document(documentId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("!!!", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val thisPackage = snapshot.toObject(Package::class.java)
                if (thisPackage != null) {
                    //Start setting values from Firebase
                    topAdressTextView.text = thisPackage.address
                   // postCodeTextView.text = thisPackage.postCodeAddress
                   // travelTimeTextView.text = thisPackage.requestedDeliveryTime
                    kmLeftTextView.text = thisPackage.kmLeft
                    
                    val timestamp = thisPackage.expectedDeliveryTime
                    val date = timestamp?.toDate() //Convert to date
                    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val dateString = format.format(date)
//                    postCodeTextView.text = dateString

                    if (!driverMode) {
                        postCodeTextView.text =
                            "Requested delivery time: ${thisPackage.requestedDeliveryTime}"
                    }
                    else {
                        postCodeTextView.text =
                            "Estimated delivery time: $dateString"
                    }
//                    postCodeTextView.text = thisPackage.expectedDeliveryTime.toString()


                    barcode = thisPackage.kolliId.toString()
                }
            } else {
                Log.d("!!!", "Current data: null")
            }
        }
    }

    private fun showMenu() {
        topAppBar.setNavigationOnClickListener {
            finish()
        }
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.user -> {
                    // Handle edit text press
                    true
                }

                R.id.help -> {
                    // Handle favorite icon press
                    true
                }

                else -> false
            }
        }
    }

    private fun initializeViews() {
        appBarLayout = findViewById(R.id.appBarLayout)
        topAppBar = findViewById(R.id.topAppBar)
        notDeliveredButton = findViewById(R.id.notDeliveredButton)
        continueDeliverButton = findViewById(R.id.continueButton)
        driveMapButton = findViewById(R.id.driveMapButton)
        topAdressTextView = findViewById(R.id.mapsTopAdressTextView)
        postCodeTextView = findViewById(R.id.mapsPostCodeTextView)
        travelTimeTextView = findViewById(R.id.travelTimeTextView)
        kmLeftTextView = findViewById(R.id.kmLeftTextView)

        mapProgressBar = findViewById(R.id.mapProgressBar)
        mapProgressBar.visibility = View.VISIBLE

    }
}