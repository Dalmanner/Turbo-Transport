package com.example.turbo_transport

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class AddPackageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_package)

        val etPackageName: EditText = findViewById(R.id.etPackageName)
        // Initialize other EditTexts <EditText
        //        android:id="@+id/etPackageName"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginStart="10dp"
        //        android:layout_marginTop="20dp"
        //        android:ems="10"
        //        android:hint="Package Name"
        //        android:text="Address"
        //        app:layout_constraintStart_toStartOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/add_top_bar" />
        //
        //    <EditText
        //        android:id="@+id/etPackageType"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginStart="10dp"
        //        android:layout_marginTop="10dp"
        //        android:ems="10"
        //        android:hint="Package Type"
        //        android:text="Package Type"
        //        app:layout_constraintStart_toStartOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etPackageName" />
        //
        //    <EditText
        //        android:id="@+id/etWeight"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginStart="10dp"
        //        android:layout_marginTop="10dp"
        //        android:layout_marginEnd="8dp"
        //        android:ems="10"
        //        android:hint="Weight"
        //        android:text="Weight"
        //        app:layout_constraintEnd_toEndOf="parent"
        //        app:layout_constraintHorizontal_bias="0.0"
        //        app:layout_constraintStart_toStartOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etPackageType" />
        //
        //    <EditText
        //        android:id="@+id/etLocation"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginStart="10dp"
        //        android:layout_marginTop="10dp"
        //        android:ems="10"
        //        android:hint="Location"
        //        android:text="Location"
        //        app:layout_constraintStart_toStartOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etWeight" />
        //
        //    <EditText
        //        android:id="@+id/etPhoneNumber"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginStart="10dp"
        //        android:layout_marginTop="10dp"
        //        android:layout_marginEnd="8dp"
        //        android:ems="10"
        //        android:hint="Phone Number"
        //        android:text="Phone Number"
        //        app:layout_constraintEnd_toEndOf="parent"
        //        app:layout_constraintHorizontal_bias="0.0"
        //        app:layout_constraintStart_toStartOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etLocation" />
        //
        //    <EditText
        //        android:id="@+id/etLatitude"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginStart="10dp"
        //        android:layout_marginTop="10dp"
        //        android:layout_marginEnd="8dp"
        //        android:ems="10"
        //        android:hint="Latitude"
        //        android:text="Latidude"
        //        app:layout_constraintEnd_toEndOf="parent"
        //        app:layout_constraintHorizontal_bias="0.0"
        //        app:layout_constraintStart_toStartOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etPhoneNumber" />
        //
        //    <EditText
        //        android:id="@+id/etLongitude"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginStart="10dp"
        //        android:layout_marginTop="10dp"
        //        android:autofillHints=""
        //        android:ems="10"
        //        android:hint="Longitude"
        //        android:text="Longitude"
        //        app:layout_constraintStart_toStartOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etLatitude"
        //        tools:ignore="TextFields" />
        //
        //    <EditText
        //        android:id="@+id/etRequestedDeliveryTime"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginStart="10dp"
        //        android:layout_marginTop="10dp"
        //        android:layout_marginEnd="8dp"
        //        android:ems="10"
        //        android:hint="Requested Delivery Time"
        //        android:text="Requested Delivery Time"
        //        app:layout_constraintEnd_toEndOf="parent"
        //        app:layout_constraintHorizontal_bias="0.0"
        //        app:layout_constraintStart_toStartOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etLongitude" />
        //
        //    <EditText
        //        android:id="@+id/etExpectedDeliveryTime"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginTop="20dp"
        //        android:layout_marginEnd="10dp"
        //        android:ems="10"
        //        android:hint="Expected Delivery Time"
        //        android:text="ETA"
        //        app:layout_constraintEnd_toEndOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/add_top_bar" />
        //
        //    <EditText
        //        android:id="@+id/etKolliId"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginTop="10dp"
        //        android:layout_marginEnd="10dp"
        //        android:ems="10"
        //        android:hint="Kolli Id"
        //        android:text="Kolli Id"
        //        app:layout_constraintEnd_toEndOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etExpectedDeliveryTime" />
        //
        //    <EditText
        //        android:id="@+id/etSenderName"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginTop="10dp"
        //        android:layout_marginEnd="10dp"
        //        android:ems="10"
        //        android:hint="Sender Name"
        //        android:text="Sender Name"
        //        app:layout_constraintEnd_toEndOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etKolliId" />
        //
        //    <EditText
        //        android:id="@+id/etDeliveryNote"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginTop="10dp"
        //        android:layout_marginEnd="10dp"
        //        android:ems="10"
        //        android:hint="Delivery Note"
        //        android:text="Delivery Note"
        //        app:layout_constraintEnd_toEndOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etSenderName" />
        //
        //    <EditText
        //        android:id="@+id/etPackageWeight"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginTop="10dp"
        //        android:layout_marginEnd="10dp"
        //        android:ems="10"
        //        android:hint="Package Weight"
        //        android:text="Package Weight"
        //        app:layout_constraintEnd_toEndOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etDeliveryNote" />
        //
        //    <EditText
        //        android:id="@+id/etPackageHeight"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginTop="10dp"
        //        android:layout_marginEnd="10dp"
        //        android:ems="10"
        //        android:hint="Package Height"
        //        android:text="Package Height"
        //        app:layout_constraintEnd_toEndOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etPackageWeight" />
        //
        //    <EditText
        //        android:id="@+id/etPackageLength"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginTop="10dp"
        //        android:layout_marginEnd="10dp"
        //        android:ems="10"
        //        android:hint="Package Length"
        //        android:text="Package Length"
        //        app:layout_constraintEnd_toEndOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etPackageHeight" />
        //
        //    <TextView
        //        android:id="@+id/add_top_bar"
        //        android:layout_width="362dp"
        //        android:layout_height="51dp"
        //        android:text="ADD PACKAGE"
        //        app:layout_constraintEnd_toEndOf="parent"
        //        app:layout_constraintStart_toStartOf="parent"
        //        app:layout_constraintTop_toTopOf="parent" />
        //
        //    <EditText
        //        android:id="@+id/etPackageDepth"
        //        android:layout_width="180dp"
        //        android:layout_height="50dp"
        //        android:layout_marginTop="10dp"
        //        android:layout_marginEnd="10dp"
        //        android:ems="10"
        //        android:hint="Package Depth"
        //        android:text="Package Depth"
        //        app:layout_constraintEnd_toEndOf="parent"
        //        app:layout_constraintTop_toBottomOf="@+id/etPackageLength" />

        val etSenderName: EditText = findViewById(R.id.etSenderName)
        val etAddress: EditText = findViewById(R.id.etPackageName)
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
        val etLocation: EditText = findViewById(R.id.etLocation)



        val btnSubmit: Button = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val packageName = etPackageName.text.toString()
            // Collect data from other EditTexts similarly

            if (packageName.isEmpty()) {
                Toast.makeText(this, "Package name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val packageInfo = Package(
                userIdReceiver = "SomeUserID", // Example data
                nameOfReceiver = packageName,
                address = "Some address", // Example data
                telephoneNumber = "1234567890", // Example data
                deliveryNote = "Some", // Example data
               // packageWeight = 10.0, // Example data
                //packageHeight = 10.0, // Example data
                //packageLength = 10.0, // Example data
                //packageDepth = 10.0, // Example data
                requestedDeliveryTime = "Some time", // Example data
                expectedDeliveryTime = "Some time", // Example data
                leaveAtTheDoor = true, // Example data
                deliveryStatus = false, // Example data
                identityCheck = true, // Example data
                kolliId = "Some ID", // Example data
                latitude = 0.0, // Example data
                longitude = 0.0, // Example data
                senderName = "Some name",
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
