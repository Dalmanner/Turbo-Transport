package com.example.turbo_transport

data class DirectionsResult(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>,
    val overview_polyline: Polyline // Denna används för att rita hela rutten
)

data class Leg(
    val steps: List<Step>,
    val distance: Distance,
    val duration: Duration
)

data class Distance(
    val value: Int,
    val text: String
)

data class Duration(
    val value: Int, // Tid i sekunder
    val text: String // Tid som läsbar text, exempelvis "1 min"
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
