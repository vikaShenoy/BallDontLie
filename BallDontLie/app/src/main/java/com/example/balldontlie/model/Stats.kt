package com.example.balldontlie.model

/**
 * Data class to represent NBA statistics.
 */
data class Stats(
    val id: Int = 0,
    val ast: Double = 0.0,
    val blk: Double = 0.0,
    val dreb: Double = 0.0,
    val fg3_pct: Double = 0.0,
    val fg3a: Double = 0.0,
    val fg3m: Double = 0.0,
    val fg_pct: Double = 0.0,
    val fga: Double = 0.0,
    val fgm: Double = 0.0,
    val ft_pct: Double = 0.0,
    val fta: Double = 0.0,
    val ftm: Double = 0.0,
    val min: String = "",
    val oreb: Double = 0.0,
    val pf: Double = 0.0,
    val pts: Double = 0.0,
    val reb: Double = 0.0,
    val stl: Double = 0.0,
    val player: Player? = null,
    val team: Team? = null,
    val game: Game? = null,
    val turnover: Double = 0.0
) {
}