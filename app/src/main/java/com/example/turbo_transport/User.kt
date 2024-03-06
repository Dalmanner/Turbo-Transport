package com.example.turbo_transport

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

 data class User(
    @DocumentId
    var documentId : String? = null,
    var userId: String? = null,
    var name : String? = null,
    var level : Int? = null, //1 = customer, 2 = driver, 3 = admin?
    var userAddress : String? = null,
    var cityName : String? = null,
    var postCode : String? = null,
    var userPhoneNumber : String? = null,
    var approveNotifications : Boolean? = null,
    var vehicleModel : String? = null,
    var vehicleId : String? = null,
    var notificationToken: String? = null,
    var Email: String? = null,
    @ServerTimestamp
    var lastEdited: Timestamp? = null,
    @ServerTimestamp
    var timestamp: Timestamp? = null) {
}