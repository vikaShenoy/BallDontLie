package com.example.balldontlie

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

/**
 * Screens available for display in the main screen, with their respective titles,
 * icons, and menu item IDs and fragments.
 */
enum class MainScreen(@IdRes val menuItemId: Int,
                      @DrawableRes val menuItemIconId: Int,
                      @StringRes val titleStringId: Int,
                      val fragment: Fragment
) {
    SCHEDULE(R.id.bottom_navigation_schedule, R.drawable.ic_schedule,
        R.string.menu_schedule, ScheduleFragment()),
    COMPARE(R.id.bottom_navigation_compare, R.drawable.ic_compare,
        R.string.menu_compare, CompareFragment()),
    PERFORMANCE(R.id.bottom_navigation_performance, R.drawable.ic_performance,
        R.string.menu_performance, PerformanceFragment())
}

fun getMainScreenForMenuItem(menuItemId: Int): MainScreen? {
    for (mainScreen in MainScreen.values()) {
        if (mainScreen.menuItemId == menuItemId) {
            return mainScreen
        }
    }
    return null
}
