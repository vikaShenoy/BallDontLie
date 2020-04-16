package com.example.balldontlie.util

import com.example.balldontlie.model.Game
import com.example.balldontlie.model.Schedule
import com.example.balldontlie.model.Stats
import com.google.gson.Gson
import org.json.JSONObject

/**
 * Cast the json stats response to a Stats object and return it.
 */
fun getStatFromReponse(response: JSONObject?): Stats? {
    var stats: Stats? = null
    val data = JSONObject(response.toString()).getJSONArray("data")
    for (i in 0 until data.length()) {
        stats = Gson().fromJson(data.getString(i), Stats::class.java)
    }
    return stats
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

fun getScheduleData(response: JSONObject?, teamId : Int) : MutableList<Schedule> {
    val gameData: ArrayList<Game> = ArrayList()
    val data = JSONObject(response.toString()).getJSONArray("data")
    for (i in 0 until data.length()) {
        gameData.add(Gson().fromJson(data.getString(i), Game::class.java))
    }
    return gamesToSchedule(gameData, teamId)
}