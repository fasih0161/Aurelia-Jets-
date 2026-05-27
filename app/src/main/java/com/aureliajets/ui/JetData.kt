package com.aureliajets.ui

import com.aureliajets.R

data class JetInfo(
    val name: String,
    val description: String,
    val seats: String,
    val range: String,
    val pricePerHour: String,
    val amenities: String,
    val imageRes: Int
)

val sampleJets = listOf(
    JetInfo(
        name = "Gulfstream G650",
        description = "A luxurious long-range private jet offering high speed, cruising up to 710 mph, with excellent fuel efficiency and premium seating.",
        seats = "Up to 18",
        range = "Long Range",
        pricePerHour = "$12,000",
        amenities = "Spacious cabin, satellite WiFi, full galley",
        imageRes = R.drawable.gulfstream_g650
    ),
    JetInfo(
        name = "Cessna Citation X",
        description = "One of the fastest business jets in the world, combining high speed with exceptional efficiency and luxury for 8-12 passengers.",
        seats = "8–12",
        range = "Mid Range",
        pricePerHour = "$7,500",
        amenities = "Leather seating, entertainment system, WiFi",
        imageRes = R.drawable.cessna_citation_x
    ),
    JetInfo(
        name = "Bombardier Global 7500",
        description = "The ultimate intercontinental jet, featuring four distinct living spaces and a master suite for maximum comfort during long flights.",
        seats = "Up to 19",
        range = "Ultra Long Range",
        pricePerHour = "$14,000",
        amenities = "4 living spaces, full kitchen, master suite",
        imageRes = R.drawable.bombardier_7500
    ),
    JetInfo(
        name = "Gulfstream G250",
        description = "A midsize luxury jet designed for agility and comfort, perfect for business trips requiring speed and a quiet flight experience.",
        seats = "8–10",
        range = "Mid Range",
        pricePerHour = "$8,000",
        amenities = "Modern cabin, WiFi, refreshment center",
        imageRes = R.drawable.gulfstream_g250
    ),
    JetInfo(
        name = "Embraer Lineage 1000E",
        description = "Large and lavish, this jet offers five cabin zones including a master bedroom with a walk-in shower for a home-like feel.",
        seats = "13–19",
        range = "Long Range",
        pricePerHour = "$11,500",
        amenities = "Master suite, five cabin zones, large baggage capacity",
        imageRes = R.drawable.embraer_lineage
    ),
    JetInfo(
        name = "Dassault Falcon 8X",
        description = "An ultra-long-range tri-jet known for its quiet cabin and ability to access challenging airports with shorter runways.",
        seats = "12–16",
        range = "Ultra Long Range",
        pricePerHour = "$13,500",
        amenities = "Quietest cabin, advanced filtration, high-speed connectivity",
        imageRes = R.drawable.falcon_8x
    )
)
