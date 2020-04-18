package com.example.balldontlie

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.balldontlie.controller.APIController
import com.example.balldontlie.controller.ServiceVolley
import com.example.balldontlie.model.SelectedPlayers
import com.example.balldontlie.util.PlayerSelect
import com.example.balldontlie.util.getRegularSeason
import com.example.balldontlie.util.getSeasonStatsFromResponse
import com.example.balldontlie.util.statCategories
import kotlinx.android.synthetic.main.fragment_compare.*
import org.json.JSONObject
import com.example.balldontlie.R
import com.example.balldontlie.model.Stats
import kotlinx.android.synthetic.main.table_row.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [CompareFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompareFragment() : Fragment() {

    private lateinit var ctx: Context
    private lateinit var controller: APIController
    private lateinit var viewInflater: LayoutInflater
    private lateinit var playerSelect: PlayerSelect

    private var selectedPlayers: SelectedPlayers = SelectedPlayers()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewInflater = inflater
        ctx = container!!.context
        controller = APIController(ServiceVolley())
        playerSelect = PlayerSelect()
        return inflater.inflate(R.layout.fragment_compare, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchButton.setOnClickListener { showSearchDialog() }
    }

    /**
     * Create and display a popup dialog used for selecting players.
     * Clear stats table.
     */
    private fun showSearchDialog() {
        statsTable.removeAllViews()
        playerSelect.selectedPlayers.clearPlayers()
        selectedPlayers.clearPlayers()
        val searchDialog = playerSelect.createSearchDialog(
            ctx, viewInflater, controller
        ) {
            selectedPlayers = playerSelect.selectedPlayers
            showPlayerStats()
        }
        searchDialog.show()
    }


    /**
     * Call the API for the selected player's current season stats.
     * Set the stats for the players.
     */
    private fun showPlayerStats() {
        val currentSeason = getRegularSeason()
        if (selectedPlayers.player1 != null) {
            controller.get(
                path = "season_averages?season=${currentSeason}&player_ids[]=${selectedPlayers.player1!!.id}",
                params = JSONObject()
            ) { response ->
                selectedPlayers.player1!!.seasonStats =
                    getSeasonStatsFromResponse(response)
                if (selectedPlayers.player2 == null) {
                    displayStatsInTable()
                }
            }
        }

        if (selectedPlayers.player2 != null) {
            controller.get(
                path = "season_averages?season=${currentSeason}&player_ids[]=${selectedPlayers.player2!!.id}",
                params = JSONObject()
            ) { response ->
                selectedPlayers.player2!!.seasonStats =
                    getSeasonStatsFromResponse(response)
                displayStatsInTable()
            }
        }
    }

    /**
     * Construct the table rows which display the selected players stats.
     */
    private fun displayStatsInTable() {
        val p1Stats = selectedPlayers.player1?.seasonStats
        val p2Stats = selectedPlayers.player2?.seasonStats

        // Header row
        val p1Name = selectedPlayers.player1!!.last_name
        var p2Name = ""
        if (selectedPlayers.player2 != null) {
            p2Name = selectedPlayers.player2!!.last_name
        }
        statsTable.addView(createHeaderRow(p1Name, p2Name))

        for (statCategory in statCategories) {
            statsTable.addView(createStatRow(p1Stats, p2Stats, statCategory))
        }
    }

    /**
     * Create a table row with player stats.
     * Check if each player stats is not null before adding the value to the row.
     * @param p1Stats player 1 stats
     * @param p2Stats player 2 stats
     * @param statCategory <"Points", "Pts"> used to retrieve the stat and display a label in table.
     * @return table row to be added to the stat table.
     */
    private fun createStatRow(
        p1Stats: Stats?,
        p2Stats: Stats?,
        statCategory: Map.Entry<String, String>
    ): View {
        val row = viewInflater.inflate(
            R.layout.table_row, statsTable,
            false
        )
        row.statP1.text = ""
        row.statLabel.text = statCategory.key
        row.statP2.text = ""

        if (p1Stats != null) {
            row.statP1.text = p1Stats.javaClass.getMethod("get${statCategory.value}")
                .invoke(p1Stats).toString()
        }

        if (p2Stats != null) {
            row.statP2.text = p2Stats.javaClass.getMethod("get${statCategory.value}")
                .invoke(p2Stats).toString()
        }

        return row
    }

    /**
     * Create the header row for the stat tables with the selected player's last names.
     * Reuses the stat table row layout.
     * @param p1Name first player's last name.
     * @param p2Name second player's last name. Could be empty.
     */
    private fun createHeaderRow(p1Name: String, p2Name: String): View {
        val row = viewInflater.inflate(
            R.layout.table_row, statsTable,
            false
        )
        row.statP1.text = p1Name
        row.statP2.text = p2Name
        return row
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CompareFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CompareFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}
