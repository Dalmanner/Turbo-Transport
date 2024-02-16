package com.example.turbo_transport

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class AddPackageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_package)

        val etSenderName: EditText = findViewById(R.id.etSenderName2)
        val etAddress: EditText = findViewById(R.id.etAddress)
        val etTelephoneNumber: EditText = findViewById(R.id.etPhoneNumber)
        val etDeliveryNote: EditText = findViewById(R.id.etDeliveryNote)
        val etPackageWeight: EditText = findViewById(R.id.etPackageWeight)
        val etPackageHeight: EditText = findViewById(R.id.etPackageHeight)
        val etPackageLength: EditText = findViewById(R.id.etPackageLength)
        val etPackageDepth: EditText = findViewById(R.id.etPackageDepth)
        val etRequestedDeliveryTime: EditText = findViewById(R.id.etRequestedDeliveryTime)
        val etExpectedDeliveryTime: EditText = findViewById(R.id.etExpectedDeliveryTime)
        val etKolliId: EditText = findViewById(R.id.etKolliId)
        val etLatitude: EditText = findViewById(R.id.etLatitude)
        val etLongitude: EditText = findViewById(R.id.etLongitude)
        val etUserIdReceiver: EditText = findViewById(R.id.etUserIdReceiver)
        val etIdentityCheck: RadioButton = findViewById(R.id.etIdentityCheck)
        val etLeaveAtTheDoor: RadioButton = findViewById(R.id.etLeaveAtTheDoor)

        val btnSubmit: ImageButton = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val userIdReceiver = etUserIdReceiver.text.toString()
            val address = etAddress.text.toString()
            val senderName = etSenderName.text.toString()
            val telephoneNumber = etTelephoneNumber.text.toString()
            val deliveryNote = etDeliveryNote.text.toString()
            val packageWeight = etPackageWeight.text.toString().toDouble()
            val packageHeight = etPackageHeight.text.toString().toDouble()
            val packageLength = etPackageLength.text.toString().toDouble()
            val packageDepth = etPackageDepth.text.toString().toDouble()
            val requestedDeliveryTime = etRequestedDeliveryTime.text.toString()
            val expectedDeliveryTime = etExpectedDeliveryTime.text.toString()
            val kolliId = etKolliId.text.toString()
            val latitude = etLatitude.text.toString().toDouble()
            val longitude = etLongitude.text.toString().toDouble()
            val identityCheck = etIdentityCheck.isChecked
            val leaveAtTheDoor = etLeaveAtTheDoor.isChecked

            if (senderName.isEmpty() || address.isEmpty() || telephoneNumber.isEmpty() || deliveryNote.isEmpty() || packageWeight.isNaN() || packageHeight.isNaN() || packageLength.isNaN() || packageDepth.isNaN() || requestedDeliveryTime.isEmpty() || expectedDeliveryTime.isEmpty() || kolliId.isEmpty() || latitude.isNaN() || longitude.isNaN() || userIdReceiver.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                Toast.makeText(this, "Package added", Toast.LENGTH_SHORT).show()
            }

            val packageInfo = Package(
                userIdReceiver = userIdReceiver,
                address = address, // Example data
                telephoneNumber = telephoneNumber, // Example data
                deliveryNote = deliveryNote, // Example data
                packageWeight = packageWeight, // Example data
                packageHeight = packageHeight, // Example data
                packageLength = packageLength, // Example data
                packageDepth = packageDepth, // Example data
                requestedDeliveryTime = requestedDeliveryTime, // Example data
                expectedDeliveryTime = expectedDeliveryTime, // Example data
                leaveAtTheDoor = leaveAtTheDoor, // Example data
                deliveryStatus = true, // Example data
                identityCheck = identityCheck, // Example data
                kolliId = kolliId, // Example data
                latitude = latitude, // Example data
                longitude = longitude, // Example data
                senderName = senderName,
                lastEdited = Timestamp.now()
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("packages")
                .add(packageInfo)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Package added with ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding package: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
