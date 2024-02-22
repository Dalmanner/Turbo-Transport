package com.example.turbo_transport

import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class AddPackageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_package)

        val etNameOfReceiver: TextInputEditText = findViewById(R.id.etNameOfReceiver)
        val etPostCodeAddress: TextInputEditText = findViewById(R.id.etPostCodeAddress)
        val etAddress: TextInputEditText = findViewById(R.id.etAddress)
        val etSenderName: TextInputEditText = findViewById(R.id.etSenderName2)
        val etTelephoneNumber: TextInputEditText = findViewById(R.id.etPhoneNumber)
        val etDeliveryNote: TextInputEditText = findViewById(R.id.etDeliveryNote)
        val etPackageWeight: TextInputEditText = findViewById(R.id.etPackageWeight)
        val etPackageHeight: TextInputEditText = findViewById(R.id.etPackageHeight)
        val etPackageLength: TextInputEditText = findViewById(R.id.etPackageLength)
        val etPackageDepth: TextInputEditText = findViewById(R.id.etPackageDepth)
        val etRequestedDeliveryTime: TextInputEditText = findViewById(R.id.etRequestedDeliveryTime)
        val etKolliId: TextInputEditText = findViewById(R.id.etKolliId)
        val etLatitude: TextInputEditText = findViewById(R.id.etLatitude)
        val etLongitude: TextInputEditText = findViewById(R.id.etLongitude)
        //val etUserIdReceiver: EditText = findViewById(R.id.etUserIdReceiver)
        val etIdentityCheck: CheckBox = findViewById(R.id.etIdentityCheck)
        val etLeaveAtTheDoor: CheckBox = findViewById(R.id.etLeaveAtTheDoor)

        val btnSubmit: ImageButton = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val nameOfReceiver = etNameOfReceiver.text.toString()
            //val userIdReceiver = etUserIdReceiver.text.toString()
            val postCodeAddress = etPostCodeAddress.text.toString()
            val address = etAddress.text.toString()
            val senderName = etSenderName.text.toString()
            val telephoneNumber = etTelephoneNumber.text.toString()
            val deliveryNote = etDeliveryNote.text.toString()
            val packageWeight = etPackageWeight.text.toString().toDouble()
            val packageHeight = etPackageHeight.text.toString().toDouble()
            val packageLength = etPackageLength.text.toString().toDouble()
            val packageDepth = etPackageDepth.text.toString().toDouble()
            val requestedDeliveryTime = etRequestedDeliveryTime.text.toString()
            val kolliId = (Math.random() * 1000000000000).toLong().toString()
            val latitude = etLatitude.text.toString().toDouble()
            val longitude = etLongitude.text.toString().toDouble()
            val identityCheck = etIdentityCheck.isChecked.or(false)
            val leaveAtTheDoor = etLeaveAtTheDoor.isChecked.or(false)

            if (nameOfReceiver.isEmpty() || senderName.isEmpty() || address.isEmpty() || postCodeAddress.isEmpty() || telephoneNumber.isEmpty() || deliveryNote.isEmpty() || packageWeight.isNaN() || packageHeight.isNaN() || packageLength.isNaN() || packageDepth.isNaN() || requestedDeliveryTime.isEmpty() || kolliId.isEmpty() || latitude.isNaN() || longitude.isNaN()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                //add log message:
                Log.d("AddPackageActivity", "Please fill in all required fields")

                return@setOnClickListener
            } else {
                Toast.makeText(this, "Package added", Toast.LENGTH_SHORT).show()
            }

            val packageInfo = Package(
                nameOfReceiver = nameOfReceiver,
                //userIdReceiver = userIdReceiver,
                address = address,
                postCodeAddress = postCodeAddress,
                telephoneNumber = telephoneNumber,
                deliveryNote = deliveryNote,
                packageWeight = packageWeight,
                packageHeight = packageHeight,
                packageLength = packageLength,
                packageDepth = packageDepth,
                requestedDeliveryTime = requestedDeliveryTime,
                leaveAtTheDoor = leaveAtTheDoor,
                identityCheck = identityCheck,
                kolliId = kolliId,
                latitude = latitude,
                longitude = longitude,
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
