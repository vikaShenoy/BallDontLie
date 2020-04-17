package com.example.balldontlie

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.balldontlie.controller.APIController
import com.example.balldontlie.controller.ServiceVolley
import com.example.balldontlie.model.SelectedPlayers
import com.example.balldontlie.util.*
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
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
        // TODO - fix api calls to depend on spinners
        val startDate = getPreviousDate(100)
        val endDate = getCurrentDate()
        val season = getRegularSeason()
        if (selectedPlayers.player1 != null) {
            controller.get(
                path = "stats?season=${season}&player_ids[]=${selectedPlayers.player1!!.id}&start_date=${startDate}&end_date=${endDate}",
                params = JSONObject()
            ) { response ->
                selectedPlayers.player1!!.gameStats = getGameStatsFromResponse(response)
                if (selectedPlayers.player2 == null) {
                    displayStatsInChart(selectedPlayers, startDate)
                }
            }
        }

        if (selectedPlayers.player2 != null) {
            controller.get(
                path = "stats?season=${season}&player_ids[]=${selectedPlayers.player2!!.id}&start_date=${startDate}&end_date=${endDate}",
                params = JSONObject()
            ) { response ->
                selectedPlayers.player2!!.gameStats = getGameStatsFromResponse(response)
                displayStatsInChart(selectedPlayers, startDate)
            }
        }
    }


    /**
     * Marshall the player stats into chart data and display it.
     * TODO - currently just a mockup. Need to implement actual player data here.
     */
    private fun displayStatsInChart(selectedPlayers: SelectedPlayers, referenceDate: String) {
        val p1Stats = selectedPlayers.player1?.gameStats
        val p2Stats = selectedPlayers.player2?.gameStats

        val lineData = LineData()

        val p1Data = getChartDataFromStats(p1Stats, referenceDate)
        val p1DataSet = LineDataSet(p1Data, "Player 1 Name")
        lineData.addDataSet(p1DataSet)

        if (p2Stats != null) {
            val p2Data = getChartDataFromStats(p2Stats, referenceDate)
            val p2DataSet = LineDataSet(p2Data, "Player 2 Name")
            lineData.addDataSet(p2DataSet)
        }

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
