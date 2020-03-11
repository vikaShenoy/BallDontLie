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
import com.google.gson.Gson
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

    fun getSchedulePreviousData(controller: APIController, teamId: Int) : ArrayList<Schedule> {
//        val path = "games?seasons[]=2019&team_ids[]=11&start_date=2019-10-22&end_date=2020-04-15"
        val path = "games"
        val params = JSONObject()
        val teams = arrayListOf<Int>(teamId)
        val seasons = arrayListOf<String>("2019")
        params.put("start_date", "2019-10-22")
        params.put("end_date", "2020-04-15")
        params.put("team_ids", teams)
        params.put("seasons", seasons)
        controller.get(path, params) { response ->
            // Data string is the array, need to iterate through it
            val dataString: String = response?.get("data").toString()
            val game = Gson().fromJson(dataString, Game::class.java)
            Log.i("info", game.toString())
        }
        return ArrayList<Schedule>()
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
