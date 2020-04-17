package com.example.balldontlie.util

val statCategories: Map<String, String> = mapOf(
    "Points" to "Pts",
    "Rebounds" to "Reb",
    "Assists" to "Ast",
    "Blocks" to "Blk",
    "Steals" to "Stl",
    "Turnovers" to "Turnover",
    "FG%" to "Fg_pct",
    "FT%" to "Ft_pct",
    "3P%" to "Fg3_pct"
)

val timeOptions: Map<String, Int> = mapOf(
    "Week" to 7, "Month" to 30, "3 Months" to 90, "6 Months" to 180, "Year" to 365
)

const val TIME_DEFAULT: String = "3 Months"

const val STATS_DEFAULT: String = "Points"

const val TEAM_DEFAULT_NAME: String = "HOU"
const val TEAM_DEFAULT_ID: Int = 11