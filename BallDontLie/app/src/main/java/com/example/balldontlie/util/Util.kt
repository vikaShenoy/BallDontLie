package com.example.balldontlie.util

import com.example.balldontlie.model.Game
import com.example.balldontlie.model.Schedule
import com.example.balldontlie.model.Stats
import com.example.balldontlie.model.Team
import com.github.mikephil.charting.data.Entry
import com.google.gson.Gson
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.annotation.StringRes
import com.example.balldontlie.R

const val DATE_PATTERN: String = "dd/MM/yyyy"

/**
 * Cast the json stats response to a Stats object and return it.
 * NOTE - there should only be one element in the data array.
 * @param response: API response.
 * @return stats object representing season averages.
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
 * Cast the json stats response to a list of Stats objects and return it.
 * @param response: API response.
 * @return ArrayList containing stats object representing multiple games stats.
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
 * @param gameStats: list of stat objects to be converted.
 * @param referenceDate: date to use in the chart data values (x value)
 * @param selectedStat: string representing the property to get from Stats at runtime
 * via reflection (eg pts or reb properties).
 * @return sorted list of <Entry> to be used as chart data.
 */
fun getChartDataFromStats(
    gameStats: MutableList<Stats>?,
    referenceDate: String,
    selectedStat: String?
): List<Entry> {
    val data = ArrayList<Entry>()
    if (gameStats != null) {
        for (stats in gameStats) {
            val date = stats.game!!.date.split("T")[0]
            val daysSince = getDaysSince(referenceDate, date)
            // TODO - investigate changing the stat model to use floats instead of doubles
            val statValue = stats.javaClass.getMethod(
                "get${selectedStat}"
            ).invoke(stats).toString().toFloat()
            data.add(Entry(daysSince, statValue))
        }
    }
    return data.sortedWith(compareBy { it.x })
}

/**
 * Get a list of schedule objects from JSON response.
 * Sort the list of schedule objects by date.
 * @param response: API response.
 * @param teamId: id of the team creating a schedule for.
 * @return a list of schedule objects to be used for schedule cards.
 */
fun getScheduleData(response: JSONObject?, teamId: Int): List<Schedule> {
    val gameData: ArrayList<Game> = ArrayList()
    val data = JSONObject(response.toString()).getJSONArray("data")
    for (i in 0 until data.length()) {
        gameData.add(Gson().fromJson(data.getString(i), Game::class.java))
    }
    return gamesToSchedule(gameData, teamId)
}

/**
 * Loop through a list of games and convert them to the format needed to be displayed
 * in schedule cards on the recycler view on the schedule fragment.
 * @param gameList: list of Games.
 * @param teamId: id of the team playing the games. Used to check whether the game is home or away.
 * @return a list of schedule objects to be used for schedule cards.
 */
fun gamesToSchedule(gameList: MutableList<Game>, teamId: Int): List<Schedule> {
    val scheduleList: MutableList<Schedule> = ArrayList<Schedule>()
    for (game in gameList) {
        if (game.home_team?.id == teamId) {
            val score: String = "${game.home_team_score} - ${game.visitor_team_score}"
            val schedule = Schedule(
                score = score,
                team = game.visitor_team!!.city,
                stadium = HOME,
                date = formatDate(game.date, DATE_PATTERN),
                win = game.home_team_score >= game.visitor_team_score
            )
            scheduleList.add(schedule)
        } else {
            val score: String = "${game.visitor_team_score} - ${game.home_team_score}"
            val schedule = Schedule(
                score = score,
                team = game.home_team!!.city,
                stadium = AWAY,
                date = formatDate(game.date, DATE_PATTERN),
                win = game.home_team_score <= game.visitor_team_score
            )
            scheduleList.add(schedule)
        }
    }
    return scheduleList.sortedByDescending { dateSorter(it.date) }
}

val dateSorter: (String) -> LocalDate = {
    LocalDate.parse(it, DateTimeFormatter.ofPattern(DATE_PATTERN))
}

/**
 * Call the API and set the team map to contain a map of ("HOU", 11) (team name to ID).
 * Init the team spinner with the contents of the map.
 * @param response: API response containing team information.
 */
fun fillTeamMap(response: JSONObject?): MutableMap<String, Int> {
    val teamMap: MutableMap<String, Int> = mutableMapOf()
    val data = JSONObject(response.toString()).getJSONArray("data")
    for (i in 0 until data.length()) {
        val team: Team = Gson().fromJson(data.getString(i), Team::class.java)
        teamMap[team.abbreviation] = team.id
    }
    return teamMap
}
