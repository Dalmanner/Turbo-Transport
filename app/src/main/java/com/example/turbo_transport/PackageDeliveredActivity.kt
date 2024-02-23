package com.example.turbo_transport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.progressindicator.LinearProgressIndicator

class PackageDeliveredActivity : AppCompatActivity() {

    private lateinit var packageDeliveredProgressBar: LinearProgressIndicator
    private lateinit var imageViewPackageDelivered: ImageView
    private lateinit var packageDeliveredTextView: TextView
    private lateinit var closeButtonDeliveredPackage: Button

    private lateinit var documentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_delivered)

        documentId = intent.getStringExtra("documentId").toString()
        initializeViews()
        packageDeliveredProgressBar.visibility = View.GONE
        closeButtonDeliveredPackage.setOnClickListener {
            goBackToStartPage()
        }

    }

    private fun goBackToStartPage(){
        //clear all running activites in the background before moving "back" in the pile, ie now
        //the "delivery loop" is finished
        val intent = Intent(this, ListDeliveries::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
    private fun initializeViews(){
        packageDeliveredProgressBar = findViewById(R.id.packageDeliveredProgressBar)
        imageViewPackageDelivered = findViewById(R.id.imageViewPackageDelivered)
        packageDeliveredTextView = findViewById(R.id.packageDeliveredTextView)
        closeButtonDeliveredPackage = findViewById(R.id.closeButtonDeliveredPackage)

    }
}