package com.example.turbo_transport

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

class Package (
    @DocumentId

var documentId : String? = null,
    var userIdReceiver: String? = null,
    var userIdDriver: String? = null,
    var nameOfReceiver : String? = null,
    var address: String? = null,
    var telephoneNumber: String? = null,
    var deliveryNote: String? = null,
    var packageWeight: Double? = null,
    var packageHeight: Double? = null,
    var packageLength: Double? = null,
    var packageDepth: Double? = null,
    var requestedDeliveryTime: String? = null,
    var expectedDeliveryTime: String? = null,
    var leaveAtTheDoor: Boolean? = null,
    var deliveryStatus: Boolean? = null,
    var identityCheck: Boolean? = null,
    var kolliId: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var senderName: String? = null,
    @ServerTimestamp
    var lastEdited: Timestamp? = null,
    @ServerTimestamp
    var timestamp: Timestamp? = null) {
    }
