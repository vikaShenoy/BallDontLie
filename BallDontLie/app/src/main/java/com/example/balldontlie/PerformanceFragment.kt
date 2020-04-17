package com.example.balldontlie

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.balldontlie.controller.APIController
import com.example.balldontlie.controller.ServiceVolley
import com.example.balldontlie.model.SelectedPlayers
import com.example.balldontlie.util.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.fragment_performance.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [PerformanceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerformanceFragment : Fragment() {

    private lateinit var ctx: Context
    private lateinit var controller: APIController
    private lateinit var playerSelect: PlayerSelect
    private lateinit var viewInflater: LayoutInflater


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ctx = container!!.context
        controller = APIController(ServiceVolley())
        playerSelect = PlayerSelect()
        viewInflater = inflater
        return inflater.inflate(R.layout.fragment_performance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        performanceSearchButton.setOnClickListener { showSearchDialog() }
    }

    private fun showSearchDialog() {
        playerSelect.selectedPlayers.clearPlayers()
        val searchDialog = playerSelect.createSearchDialog(
            ctx, viewInflater, controller
        ) {
            fillGraphWithStats()
        }
        searchDialog.show()
    }

    /**
     * Call the API for the selected player's current season stats.
     * Set the stats for the players.
     * Display the player stats in a graph.
     */
    private fun fillGraphWithStats() {
        val selectedPlayers = playerSelect.selectedPlayers
        val startDate = getPreviousDate(daysAgo = 100)
        val endDate = getCurrentDate()
        val season = getRegularSeason()
        if (selectedPlayers.player1 != null) {
            controller.get(
                path = "stats?season=${season}&player_ids[]=${selectedPlayers.player1!!.id}&start_date=${startDate}&end_date=${endDate}",
                params = JSONObject()
            ) { response ->
                selectedPlayers.player1!!.gameStats = getGameStatsFromResponse(response)
                if (selectedPlayers.player2 == null) {
                    displayStatsInChart(selectedPlayers)
                }
            }
        }

        if (selectedPlayers.player2 != null) {
            controller.get(
                path = "stats?season=${season}&player_ids[]=${selectedPlayers.player2!!.id}&start_date=${startDate}&end_date=${endDate}",
                params = JSONObject()
            ) { response ->
                selectedPlayers.player2!!.gameStats = getGameStatsFromResponse(response)
                displayStatsInChart(selectedPlayers)
            }
        }
    }


    /**
     * Marshall the player stats into chart data and display it.
     * TODO - currently just a mockup. Need to implement actual player data here.
     */
    private fun displayStatsInChart(selectedPlayers: SelectedPlayers) {
        val player1Stats = selectedPlayers.player1?.gameStats
        val player2Stats = selectedPlayers.player2?.gameStats

        val dataList1 = ArrayList<Entry>()
        dataList1.add(Entry("2016".toFloat(), 500.0F))
        dataList1.add(Entry("2017".toFloat(), 600.0F))

        val dataList2 = ArrayList<Entry>()
        dataList2.add(Entry("2016".toFloat(), 300.0F))
        dataList2.add(Entry("2017".toFloat(), 400.0F))

        val dataSet1 = LineDataSet(dataList1, "Player1Label")
        val dataSet2 = LineDataSet(dataList2, "Player2Label")

        val lineData = LineData(dataSet1, dataSet2)

        statsChart.data = lineData
        statsChart.invalidate()

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PerformanceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PerformanceFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}
