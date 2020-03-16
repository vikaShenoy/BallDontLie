package com.example.balldontlie.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balldontlie.R
import com.example.balldontlie.controller.*
import com.example.balldontlie.model.Game
import com.example.balldontlie.model.Schedule
import com.google.gson.Gson
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var teamId: Int = 11
    private var scheduleData: MutableList<Schedule> = ArrayList<Schedule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(
            R.layout.fragment_schedule,
            container,
            false
        )
        viewAdapter = ScheduleAdapter(scheduleData)
        viewManager = LinearLayoutManager(activity)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val service: ServiceInterface = ServiceVolley()
        val apiController = APIController(service)
        // TODO - get teamID dynamically
        val currentSeason = getCurrentSeason()
        // TODO - get date ranges based on spinner
        getGames(
            controller = apiController,
            teamId = teamId,
            startDate = "2019-10-22",
            endDate = "2020-04-15",
            season = currentSeason
        )
        return rootView
    }

    private fun getGames(
        controller: APIController,
        teamId: Int,
        startDate: String,
        endDate: String,
        season: Int
    ) {
        val path = "games?start_date=${startDate}&end_date=${
        endDate}&seasons[]=${season}&team_ids[]=${teamId}"
        controller.get(path = path, params = JSONObject()) { response ->
            setScheduleData(response)
        }
    }

    private fun setScheduleData(response: JSONObject?) {
        val gameData: ArrayList<Game> = ArrayList<Game>()
        val data = JSONObject(response.toString()).getJSONArray("data")
        for (i in 0 until data.length()) {
            gameData.add(Gson().fromJson(data.getString(i), Game::class.java))
        }
        val scheduleList = gamesToSchedule(gameData, teamId)
        updateView(scheduleList)
    }

    private fun updateView(data: List<Schedule>) {
        scheduleData.clear()
        scheduleData.addAll(data)
        viewAdapter.notifyDataSetChanged()
    }





    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScheduleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
