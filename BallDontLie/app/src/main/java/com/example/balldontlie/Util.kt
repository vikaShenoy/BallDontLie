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
    "Points" to "pts",
    "Rebounds" to "reb",
    "Assists" to "ast",
    "Blocks" to "blk",
    "Steals" to "stl",
    "Turnovers" to "turnover",
    "FG%" to "fg_pct",
    "FT%" to "ft_pct",
    "3P%" to "fg3_pct"
)