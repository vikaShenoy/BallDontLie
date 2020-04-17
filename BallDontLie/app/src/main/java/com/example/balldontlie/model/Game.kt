package com.example.balldontlie.model

/**
 * Represents an NBA game.
 */
data class Game(
    val id: Int = 0,
    val date: String = "",
    val home_team_score: Int = 0,
    val visitor_team_score: Int = 0,
    val season: Int = 0,
    val period: Int = 0,
    val status: String = "",
    val time: String = "",
    val postseason: Boolean = false,
    val home_team: Team? = null,
    val visitor_team: Team? = null
) {
}