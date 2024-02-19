package com.example.turbo_transport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar

class CustomerDeliveryActivity : AppCompatActivity() {

    private lateinit var appBarLayout: AppBarLayout
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var signatureButton: Button
    private lateinit var leaveAtTheDoorButton: Button
    private lateinit var documentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_delivery)

        initializeViews()
        showMenu()
        documentId = intent.getStringExtra("documentId").toString()
        signatureButton.setOnClickListener {
            sendToSignatureActivity()
        }
    }
    private fun showMenu(){
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.user -> {
                    // Handle edit text press
                    true
                }
                R.id.help -> {
                    val intent = Intent(this, ListDeliveries::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
    private fun sendToSignatureActivity() {

        val intent = Intent(this, SignatureActivity::class.java)
        intent.putExtra("documentId", documentId)
        startActivity(intent)
    }

    private fun initializeViews() {
        appBarLayout = findViewById(R.id.appBarLayout)
        topAppBar = findViewById(R.id.topAppBar)
        signatureButton = findViewById(R.id.signaturebutton)
        leaveAtTheDoorButton = findViewById(R.id.leavatthedoorbutton)

    }
}