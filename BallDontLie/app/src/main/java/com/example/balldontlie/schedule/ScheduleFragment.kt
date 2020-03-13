package com.example.balldontlie.schedule

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balldontlie.R
import com.example.balldontlie.controller.APIController
import com.example.balldontlie.controller.ServiceInterface
import com.example.balldontlie.controller.ServiceVolley
import com.example.balldontlie.model.Game
import com.example.balldontlie.model.Schedule
import com.google.gson.Gson
import com.google.gson.JsonArray
import org.json.JSONArray
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter : RecyclerView.Adapter<*>
    private lateinit var viewManager : RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_schedule, container, false)

        val service : ServiceInterface = ServiceVolley()
        val apiController: APIController = APIController(service)
        // TODO - Get the team ID from drop down here
        val teamId = 11
        val data = getSchedulePreviousData(apiController, teamId)
        viewAdapter = ScheduleAdapter(data)
        viewManager = LinearLayoutManager(activity)
        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        return rootView
    }

    private fun getSchedulePreviousData(controller: APIController, teamId: Int) : MutableList<Schedule> {
        val gameList: MutableList<Game> = ArrayList()

        var path = "games?"
        path += "start_date=2019-10-22"
        path += "&end_date=2020-04-15"
        path += "&seasons[]=2019"
        path += "&team_ids[]=$teamId"

        // TODO - fix the control flow so that the data is returned
        controller.get(path=path, params= JSONObject()) { response ->
            val gson = Gson()
            val jsonObj = JSONObject(response.toString())
            val gameArray = jsonObj.getJSONArray("data")
            for (i in 0 until gameArray.length()) {
                val game: Game = gson.fromJson(gameArray.getString(i), Game::class.java)
                gameList.add(game)
                Log.i("Checks", "Size after adding = ${gameList.size.toString()}")
            }
        }
        return gamesToSchedule(gameList, 11)
    }

    private fun gamesToSchedule(gameList : MutableList<Game>, teamId: Int) : MutableList<Schedule> {
        val scheduleList: MutableList<Schedule> = ArrayList<Schedule>()
        for (game in gameList) {
            if (game.home_team?.id == teamId) {
                val score: String = "${game.home_team_score}-${game.visitor_team_score}"
                val schedule = Schedule(
                    score=score,
                    team=game.home_team.full_name,
                    stadium=game.home_team.city
                )
                scheduleList.add(schedule)
            } else {
                val score: String = "${game.visitor_team_score}-${game.home_team_score}"
                val schedule = Schedule(
                    score=score,
                    team= game.visitor_team!!.full_name,
                    stadium= game.home_team!!.city
                )
                scheduleList.add(schedule)
            }
        }
        return scheduleList
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
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
