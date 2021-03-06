package com.example.balldontlie.model

/**
 * Data class to represent an NBA team.
 */
data class Team(
    val id: Int = 0, val abbreviation: String = "", val city: String = "",
    val conference: String = "", val division: String = "", val full_name: String = "",
    val name: String = ""
) {
}