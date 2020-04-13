package com.example.balldontlie

import com.example.balldontlie.model.Stats
import com.google.gson.Gson
import org.json.JSONObject

/**
 * Cast the json stats response to a Stats object and return it.
 */
fun getStatsFromResponse(response: JSONObject?): Stats? {
    var stats: Stats? = null
    val data = JSONObject(response.toString()).getJSONArray("data")
    for (i in 0 until data.length()) {
        stats = Gson().fromJson(data.getString(i), Stats::class.java)
    }
    return stats
}

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