package com.example.turbo_transport

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_ALL_PERMISSIONS = 101
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var emailView: TextInputLayout
    private lateinit var passwordView: TextInputLayout
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText

    private lateinit var viewsToFade: List<View>

    private lateinit var signupView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        db = Firebase.firestore
       // auth.signOut()

        initializeViews()
        requestPermission()

        signupView.setOnClickListener {
            goToSignUp()
        }

        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            Log.d("!!!", "pressed")
            login()
        }


        //Keep user logged in
       if (auth.currentUser != null) {
//            auth.signOut()
            val user = auth.currentUser
            if (user != null) {
                db.collection("users").document(user.uid)
                    .get().addOnSuccessListener { document ->
                        val item = document.toObject(User::class.java)
                        Log.d("!!!", "User ID is: ${user.uid}")
                        if (item?.level == 1) {
                            val intent = Intent(this, ListReceiverPackage::class.java)
                            startActivity(intent)
                        }  else if (item?.level == 2){
                            val intent = Intent(this, ListDeliveries::class.java)
                            startActivity(intent)

                        }
                    }
            }

            //val intent = Intent(this, ListDeliveries::class.java)
           // startActivity(intent)
        }





        //Hide keyboard on enter
        email.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                password.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }
        password.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                hideKeyboard()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun Activity.hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val currentFocusedView = currentFocus
        currentFocusedView?.let {
            inputMethodManager?.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }

    private fun login() {

        val emailLogin = email.text.toString().trim()
        val passwordLogin = password.text.toString().trim()

        if (emailLogin.isEmpty()) {
            return
        }
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!emailLogin.matches(emailPattern.toRegex())) {
            return
        }
        if (passwordLogin.isEmpty() || passwordLogin.length < 6) {
            return
        }

        emailView.error = null
        passwordView.error = null

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        progressBar.visibility = View.VISIBLE
        fadeViews(viewsToFade, true)

        auth.signInWithEmailAndPassword(emailLogin, passwordLogin)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    progressBar.visibility = View.GONE
                    if (user != null) {
                        db.collection("users").document(user.uid)
                            .get().addOnSuccessListener { document ->
                                val item = document.toObject(User::class.java)
                                Log.d("!!!", "User ID is: ${user.uid}")
                                if (item?.level == 1) {
                                    val intent = Intent(this, ListReceiverPackage::class.java)
                                    startActivity(intent)
                                }  else if (item?.level == 2){
                                    val intent = Intent(this, ListDeliveries::class.java)
                                    startActivity(intent)

                                }
                            }
                    }
                    else {
                        //val intent = Intent(this, ListDeliveries::class.java)
                        //tartActivity(intent)
                    }
                    Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener {

                Log.d("!!!", "$it")
                progressBar.visibility = View.GONE
                emailView.error = "Wrong e-mail or password"
                passwordView.error = "Wrong e-mail or password"
                fadeViews(viewsToFade, false)
            }
    }

    private fun goToSignUp() {
        val intent = Intent(this, PackageActivity::class.java)
        startActivity(intent)
    }

    private fun fadeViews(views: List<View>, fadeOut: Boolean) {
        val alphaValue = if (fadeOut) 0.5f else 1f

        views.forEach { view ->
            view.animate()
                .alpha(alphaValue)
                .setDuration(2000)
                .setListener(null)
        }
    }

    private fun requestPermission() {
        //First check permissions
        if (!checkPermissions()) {

            // Request permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_ALL_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ALL_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Location permissions granted, continue to log in.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Permission denied, app will not be able to show your position",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun initializeViews() {
        emailView = findViewById(R.id.emailView)
        passwordView = findViewById(R.id.passwordView)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        viewsToFade = listOf(
            emailView, passwordView, email, password
        )
        signupView = findViewById(R.id.sigupView)
    }
}