package com.example.balldontlie.schedule

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balldontlie.R
import com.example.balldontlie.controller.*
import com.example.balldontlie.model.Game
import com.example.balldontlie.model.Schedule
import com.example.balldontlie.model.Team
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_schedule.*
import org.json.JSONObject
import java.text.FieldPosition

/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var controller: APIController
    private lateinit var ctx: Context
    private var teamId: Int = 11
    private var scheduleData: MutableList<Schedule> = ArrayList<Schedule>()

    private var teamMap = mutableMapOf<String, Int>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            ctx = container.context
        }

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
        controller = APIController(service)

        // Call API to fill a map of {"HOU"->11...}
        controller.get("teams", JSONObject()) { response ->
            fillTeamMap(response)
        }

        getGames(
            controller = controller,
            teamId = teamId,
            startDate = getCurrentDate(),
            endDate = getSeasonEndDate()
        )
        return rootView
    }

    private fun fillTeamMap(response: JSONObject?) {
        val data = JSONObject(response.toString()).getJSONArray("data")
        for (i in 0 until data.length()) {
            val team : Team = Gson().fromJson(data.getString(i), Team::class.java)
            teamMap[team.abbreviation] = team.id
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ArrayAdapter.createFromResource(
            ctx,
            R.array.season_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            seasonSpinner.adapter = adapter
        }

        seasonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                idp3: Long
            ) {
                if (parent != null) {
                    seasonSelected(parent, position)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        // TODO: team spinner functionality
        // teamMap = {"HOU": 11 ... }
        // have the teamId variable
        // set an adapter for the team listener to repopulate cards based on selected teams
    }

    private fun seasonSelected(parent: AdapterView<*>, position: Int) {
        val selectedItem = parent.getItemAtPosition(position)
        var startDate = ""
        var endDate = ""

        if (selectedItem == "Previous") {
            startDate = getSeasonStartDate()
            endDate = getCurrentDate()
        } else if (selectedItem == "Current") {
            startDate = getCurrentDate()
            endDate = startDate
        } else if (selectedItem == "Upcoming") {
            startDate = getCurrentDate()
            endDate = getSeasonEndDate()
        }

        getGames(
            controller = controller,
            teamId = teamId,
            startDate = startDate,
            endDate = endDate
        )
    }

    private fun getGames(
        controller: APIController,
        teamId: Int,
        startDate: String,
        endDate: String
    ) {
        val path = "games?start_date=${startDate}&end_date=${
        endDate}&team_ids[]=${teamId}"
        Log.i("check", path)
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
