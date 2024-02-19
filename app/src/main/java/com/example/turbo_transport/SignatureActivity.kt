package com.example.turbo_transport

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SignatureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)

        val clearButton = findViewById<Button>(R.id.clear_button)
        val signatureView = findViewById<SignatureView>(R.id.signature_view)

        clearButton.setOnClickListener {
            signatureView.clearSignature()
        }
    }
}
