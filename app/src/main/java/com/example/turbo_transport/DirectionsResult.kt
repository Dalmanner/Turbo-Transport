package com.example.turbo_transport

data class DirectionsResult(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>,
    val overview_polyline: Polyline // Denna används för att rita hela rutten
)

data class Leg(
    val steps: List<Step>
)

data class Step(
    val start_location: Location,
    val end_location: Location,
    val polyline: Polyline
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Polyline(
    val points: String // Encoded polyline string
)
