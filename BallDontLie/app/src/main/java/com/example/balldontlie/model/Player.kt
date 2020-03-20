package com.example.balldontlie.model

data class Player(
    val id: Int = 0,
    val first_name: String = "",
    val last_name: String = "",
    val position: String = "",
    val height_feet: Int = 0,
    val height_inches: Int = 0,
    val weight_pounds: Int = 0,
    val team: Team? = null
) {
}