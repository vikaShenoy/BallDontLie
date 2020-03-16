package com.example.balldontlie.schedule

import com.example.balldontlie.model.Game
import com.example.balldontlie.model.Schedule
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
 * Gets the current NBA season.
 * The most recent season ran from October 22 - June 21 (including playoffs), so if the current
 * date is before June 21, then the season will be the previous year, else it's the current year.
 */
fun getCurrentSeason() : Int {
    val currentDate = Calendar.getInstance().time
    val formatter = SimpleDateFormat("MM-dd")
    val formattedDate = formatter.format(currentDate)
    val currentYear =  Calendar.getInstance().get(Calendar.YEAR)
    val seasonEndDate = "06-21"
    if (formattedDate < seasonEndDate) {
        return currentYear - 1
    }
    return currentYear
}