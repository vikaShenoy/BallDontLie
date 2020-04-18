package com.example.balldontlie

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balldontlie.adapter.ScheduleAdapter
import com.example.balldontlie.controller.*
import com.example.balldontlie.model.Game
import com.example.balldontlie.model.Schedule
import com.example.balldontlie.model.Team
import com.example.balldontlie.util.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_schedule.*
import org.honorato.multistatetogglebutton.MultiStateToggleButton
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
    private lateinit var controller: APIController
    private lateinit var ctx: Context
    private var scheduleData = ArrayList<Schedule>()

    private var teamMap = mutableMapOf<String, Int>()

    private var teamId: Int = TEAM_DEFAULT_ID
    private var startDate = getSeasonStartDate()
    private var endDate = getCurrentDate()

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
        viewAdapter =
            ScheduleAdapter(scheduleData, ctx)
        viewManager = LinearLayoutManager(activity)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        val service: ServiceInterface = ServiceVolley()
        controller = APIController(service)

        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller.get("teams", JSONObject()) { response ->
            teamMap = fillTeamMap(response)
            initTeamSpinner()
            initSeasonToggle()
        }
    }


    /**
     * Set the season toggle elements.
     */
    private fun initSeasonToggle() {
        val toggle: MultiStateToggleButton? = activity?.findViewById(R.id.season_toggle)
        toggle?.setElements(R.array.season_array, 0)
        toggle?.setOnValueChangedListener { position ->
            val selectedSeason = resources.getStringArray(R.array.season_array)[position]
            setSeason(selectedSeason)
        }
    }


    /**
     * Set the team spinner to contain the items from the team map.
     */
    private fun initTeamSpinner() {
        ArrayAdapter(
            ctx,
            android.R.layout.simple_spinner_dropdown_item,
            teamMap.keys.toTypedArray()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            teamSpinner.adapter = adapter
            teamSpinner.setSelection(adapter.getPosition(TEAM_DEFAULT_NAME))
        }
        teamSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedTeam = parent?.getItemAtPosition(position) as String
                setTeamId(selectedTeam)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

    }

    /**
     * Called when the user changes the season toggle.
     * Update the date state.
     * @param selectedSeason represents prev, current, future games.
     */
    private fun setSeason(selectedSeason: String) {
        when (selectedSeason) {
            getString(R.string.season_past) -> {
                startDate = getSeasonStartDate()
                endDate = getCurrentDate()
            }
            getString(R.string.season_present) -> {
                startDate = getCurrentDate()
                endDate = getCurrentDate()
                Toast.makeText(ctx, "Enjoy today's games!", Toast.LENGTH_SHORT).show()

            }
            getString(R.string.season_future) -> {
                startDate = getCurrentDate()
                endDate = getSeasonEndDate()
            }
        }
        refreshSchedule()
    }

    /**
     * Update the selected team state.
     */
    private fun setTeamId(selectedTeam: String) {
        teamId = teamMap[selectedTeam]!!
        refreshSchedule()
    }

    /**
     * Make an API call for fresh data.
     * Called when the team or season state changes.
     */
    private fun refreshSchedule() {
        val path = "games?start_date=${startDate}&end_date=${
        endDate}&team_ids[]=${teamId}&per_page=100"
        controller.get(path = path, params = JSONObject()) { response ->
            val scheduleData = getScheduleData(response, teamId)
            updateView(scheduleData)
        }
    }


    /**
     * Clear previous data and set the schedule list to display fresh data.
     * @param data: Schedule data to display on the cards.
     */
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
