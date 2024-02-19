package com.example.turbo_transport

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SignatureActivity : AppCompatActivity() {

    private lateinit var documentId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)

        val clearButton = findViewById<Button>(R.id.clear_button)
        val signatureView = findViewById<SignatureView>(R.id.signature_view)
        val confirmButton = findViewById<Button>(R.id.confirmsignaturebutton)

        documentId = intent.getStringExtra("documentId").toString()

        clearButton.setOnClickListener {
            signatureView.clearSignature()
        }
        confirmButton.setOnClickListener {
            //Send to deliveredpackage activity for further actions
            val intent = Intent(
                this,
                PackageDeliveredActivity::class.java
            ).apply {
                putExtra("documentId", documentId)
            }
            startActivity(intent)
        }
    }
}
