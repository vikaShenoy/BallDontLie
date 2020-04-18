package com.example.balldontlie.model

/**
 * Represents a game between two teams. Used in the schedule fragment list.
 * @param team: opposition team.
 */
data class Schedule(
    val score: String = "",
    val team: String = "",
    val stadium: String = "",
    val date: String = "",
    val win: Boolean = false
) {}