package com.st10090542.birdlensapp

data class HotspotResponse(val hotspots: List<Hotspot>)

data class Hotspot(val name: String, val latitude: Double, val longitude: Double)
