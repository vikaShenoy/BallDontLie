package com.example.balldontlie.util

import android.util.Log
import com.example.balldontlie.model.Game
import com.example.balldontlie.model.Schedule
import com.example.balldontlie.model.Stats
import com.github.mikephil.charting.data.Entry
import com.google.gson.Gson
import org.json.JSONObject

/**
 * Cast the json stats response to a Stats object and return it.
 */
fun getSeasonStatsFromResponse(response: JSONObject?): Stats? {
    var stats: Stats? = null
    val data = JSONObject(response.toString()).getJSONArray("data")
    for (i in 0 until data.length()) {
        stats = Gson().fromJson(data.getString(i), Stats::class.java)
    }
    return stats
}

/**
 * Cast the json stats response to a Stats object and return it.
 */
fun getGameStatsFromResponse(response: JSONObject?): MutableList<Stats> {
    val stats = ArrayList<Stats>()
    val data = JSONObject(response.toString()).getJSONArray("data")
    for (i in 0 until data.length()) {
        stats.add(Gson().fromJson(data.getString(i), Stats::class.java))
    }
    return stats
}

/**
 * Iterate through game stats list and marshal the stats into a data list for the stats chart.
 * Becomes a list of (x=Days ago, y=points) Entry objects.
 * Sort based on the x values as this is required by MPAndroidChart.
 */
fun getChartDataFromStats(gameStats: MutableList<Stats>?, referenceDate: String): List<Entry> {
    // Days ago - stats
    val data = ArrayList<Entry>()
    if (gameStats != null) {
        for (stats in gameStats) {
            val date = stats.game!!.date.split("T")[0]
            Log.i("statscheck", "ref: ${referenceDate}, date: $date")
            val daysSince = getDaysSince(referenceDate, date)
            data.add(Entry(daysSince, stats.pts.toFloat()))
        }
    }
    val sortedData = data.sortedWith(compareBy { it.x })
    Log.i("stats", sortedData.toString())
    return sortedData
}

/**
 * Loop through a list of games and convert them to the format needed to be displayed
 * in schedule cards on the recycler view on the schedule fragment.
 */
fun gamesToSchedule(gameList: MutableList<Game>, teamId: Int): MutableList<Schedule> {
    val scheduleList: MutableList<Schedule> = ArrayList<Schedule>()
    for (game in gameList) {
        if (game.home_team?.id == teamId) {
            val score: String = "${game.home_team_score} - ${game.visitor_team_score}"
            val schedule = Schedule(
                score = score,
                team = game.visitor_team!!.full_name,
                stadium = game.home_team.city
            )
            scheduleList.add(schedule)
        } else {
            val score: String = "${game.visitor_team_score} - ${game.home_team_score}"
            val schedule = Schedule(
                score = score,
                team = game.home_team!!.full_name,
                stadium = game.home_team.city
            )
            scheduleList.add(schedule)
        }
    }
    return scheduleList
}

fun getScheduleData(response: JSONObject?, teamId: Int): MutableList<Schedule> {
    val gameData: ArrayList<Game> = ArrayList()
    val data = JSONObject(response.toString()).getJSONArray("data")
    for (i in 0 until data.length()) {
        gameData.add(Gson().fromJson(data.getString(i), Game::class.java))
    }
    return gamesToSchedule(gameData, teamId)
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