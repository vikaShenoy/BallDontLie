package com.example.balldontlie.schedule

import com.example.balldontlie.controller.APIController
import com.example.balldontlie.model.Game
import com.example.balldontlie.model.Schedule
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

/**
 * Return the current date.
 * Format is a string of yyyy-MM-dd as this is what the balldontlie api takes for date params.
 */
fun getCurrentDate(): String {
    val currentDate = Calendar.getInstance().time
    val apiDatePattern = "yyyy-MM-dd"
    val formatter = SimpleDateFormat(apiDatePattern)
    return formatter.format(currentDate)
}

/**
 * Returns the start date of the current nba season. Seasons start on 22 Oct and run till 21 June.
 * Format is a string of yyyy-MM-dd as this is what the balldontlie api takes for date params.
 */
fun getSeasonStartDate(): String {
    val currentDate = Calendar.getInstance().time
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val pattern = "MM-dd"
    val formatter = SimpleDateFormat(pattern)
    val formattedCurrentDate = formatter.format(currentDate)

    val seasonStartDate = "10-22"
    val seasonEndDate = "06-21"
    if (formattedCurrentDate < seasonEndDate) {
        return "${currentYear - 1}-${seasonStartDate}"
    }
    return "${currentYear}-${seasonStartDate}"
}

/**
 * Returns the end date of the current nba season. Seasons start on 21 Oct and run till June.
 * Format is a string of yyyy-MM-dd as this is what the balldontlie api takes for date params.
 */
fun getSeasonEndDate(): String {
    val currentDate = Calendar.getInstance().time
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val pattern = "MM-dd"
    val formatter = SimpleDateFormat(pattern)
    val formattedDate = formatter.format(currentDate)
    val seasonEndDate = "06-21"
    if (formattedDate > seasonEndDate) {
        return "${currentYear + 1}-${seasonEndDate}"
    }
    return "${currentYear}-${seasonEndDate}"
}

/**
 * Get the year of the current nba regular season (used for season average stats).
 * If before June 21, then it's the previous year, otherwise it's the current.
 */
fun getRegularSeason(): String {
    val currentDate = Calendar.getInstance().time
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val pattern = "MM-dd"
    val formatter = SimpleDateFormat(pattern)
    val formattedCurrentDate = formatter.format(currentDate)

    val seasonEndDate = "06-21"
    if (formattedCurrentDate < seasonEndDate) {
        return (currentYear - 1).toString()
    }
    return currentYear.toString()

}