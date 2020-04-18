package com.example.balldontlie.util

import java.text.SimpleDateFormat
import java.util.*

// Date format returned in BallDontLie's data.
const val API_DATE_PATTERN: String = "yyyy-MM-dd"

/**
 * Return the current date.
 * Format is a string of yyyy-MM-dd as this is what the balldontlie api takes for date params.
 */
fun getCurrentDate(): String {
    val currentDate = Calendar.getInstance().time
    val formatter = SimpleDateFormat(API_DATE_PATTERN)
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

/**
 * Subtract a number of days from the current date and return.
 * @param daysAgo: number of days to subtract.
 * @return String representing the previous.
 */
fun getPreviousDate(daysAgo: Int?): String {
    val formatter = SimpleDateFormat(API_DATE_PATTERN)

    val date = Calendar.getInstance()
    date.add(Calendar.DAY_OF_YEAR, -1 * daysAgo!!)
    val previousDate = date.time
    return formatter.format(previousDate)
}


/**
 * Find the difference (in number of days) between the two dates passed.
 * @param referenceDate: date to find days from.
 * @param date: date to find days to.
 * @return number of days between the two dates.
 */
fun getDaysSince(referenceDate: String, date: String): Float {
    val formatter = SimpleDateFormat(API_DATE_PATTERN)

    val formattedRefDate = formatter.parse(referenceDate)
    val formattedDate = formatter.parse(date)

    val differenceInTime = (formattedDate.time - formattedRefDate.time).toFloat()
    return differenceInTime / (1000 * 3600 * 24)

}

/**
 * Format the date with the new pattern provided.
 * @param dateTime string containing the date and time. Needs to be split.
 * @param newPattern pattern to return the string with.
 * @return formatted date string.
 */
fun formatDate(dateTime: String, newPattern: String): String {
    val formatter = SimpleDateFormat(API_DATE_PATTERN)
    val newFormatter = SimpleDateFormat(newPattern)

    val dateString = dateTime.split("T")[0]
    val date = formatter.parse(dateString)
    return newFormatter.format(date)
}