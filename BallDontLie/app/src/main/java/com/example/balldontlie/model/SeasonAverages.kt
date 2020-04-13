package com.example.balldontlie.model

data class SeasonAverages(
    val games_played: Int = 0,
    val player_id: Int = 0,
    val season: Int = 0,
    val ast: Int = 0,
    val blk: Int = 0,
    val dreb: Int = 0,
    val fg3_pct: Double = 0.0,
    val fg3a: Int = 0,
    val fg3m: Int = 0,
    val fg_pct: Double = 0.0,
    val fga: Int = 0,
    val fgm: Int = 0,
    val ft_pct: Double = 0.0,
    val fta: Int = 0,
    val ftm: Int = 0,
    val min: String = "",
    val oreb: Int = 0,
    val pf: Int = 0,
    val pts: Int = 0,
    val reb: Int = 0,
    val stl: Int = 0,
    val turnover: Int = 0
) {
}