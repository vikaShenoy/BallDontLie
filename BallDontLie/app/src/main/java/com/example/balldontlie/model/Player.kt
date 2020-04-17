package com.example.balldontlie.model

/**
 * Represents an NBA player.
 */
data class Player(
    val id: Int = 0,
    val first_name: String = "",
    val last_name: String = "",
    val position: String = "",
    val height_feet: Int = 0,
    val height_inches: Int = 0,
    val weight_pounds: Int = 0,
    val team: Team? = null,
    // Statistic averages over a season
    var seasonStats: Stats? = null,
    // List of statistics representing stats in games
    var gameStats: MutableList<Stats> = ArrayList<Stats>()

) {

    override fun toString(): String {
        if (team != null) {
            return "$first_name $last_name - ${team.abbreviation}"
        }
        return "$first_name $last_name"
    }
}