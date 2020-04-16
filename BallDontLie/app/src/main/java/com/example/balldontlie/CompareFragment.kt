package com.example.balldontlie

import android.content.Context
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
import com.example.balldontlie.util.getStatsFromResponse
import com.example.balldontlie.util.statCategories
import kotlinx.android.synthetic.main.fragment_compare.*
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Use the [CompareFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompareFragment() : Fragment() {

    private lateinit var ctx: Context
    private lateinit var controller: APIController
    private lateinit var myInflater: LayoutInflater

    private var playerSelect: PlayerSelect =
        PlayerSelect()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myInflater = inflater
        ctx = container!!.context
        controller = APIController(ServiceVolley())
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
        val searchDialog = playerSelect.createSearchDialog(
            ctx, myInflater, controller
        ) { showPlayerStats() }
        searchDialog.show()
    }


    /**
     * Call the API for the selected player's current season stats.
     * Set the stats for the players.
     */
    private fun showPlayerStats() {
        val selectedPlayers = playerSelect.selectedPlayers
        val currentSeason = getRegularSeason()
        if (selectedPlayers.player1 != null) {
            controller.get(
                path = "season_averages?season=${currentSeason}&player_ids[]=${selectedPlayers.player1!!.id}",
                params = JSONObject()
            ) { response ->
                selectedPlayers.player1!!.stats =
                    getStatsFromResponse(response)
                if (selectedPlayers.player2 == null) {
                    displayPlayerStats(selectedPlayers)
                }
            }
        }

        if (selectedPlayers.player2 != null) {
            controller.get(
                path = "season_averages?season=${currentSeason}&player_ids[]=${selectedPlayers.player2!!.id}",
                params = JSONObject()
            ) { response ->
                selectedPlayers.player2!!.stats =
                    getStatsFromResponse(response)
                displayPlayerStats(selectedPlayers)
            }
        }
    }

    /**
     * Construct the table rows which display the selected players stats.
     */
    private fun displayPlayerStats(selectedPlayers : SelectedPlayers) {
        val player1Stats = selectedPlayers.player1?.stats
        val player2Stats = selectedPlayers.player2?.stats

        val header = TableRow(ctx)
        header.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val player1Name = TextView(ctx)
        val filler = TextView(ctx)
        val player2Name = TextView(ctx)
        player1Name.text = ""
        player2Name.text = ""
        filler.text = ""

        if (selectedPlayers.player1 != null) {
            player1Name.text = selectedPlayers.player1!!.last_name
        }

        if (selectedPlayers.player2 != null) {
            player2Name.text = selectedPlayers.player2!!.last_name
        }

        header.addView(player1Name)
        header.addView(filler)
        header.addView(player2Name)
        statsTable.addView(header)

        for (statCategory in statCategories) {
            val row = TableRow(ctx)
            row.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val statP1 = TextView(ctx)
            val statP2 = TextView(ctx)
            val statLabel = TextView(ctx)
            statP1.text = ""
            statP2.text = ""
            statLabel.text = statCategory.key

            if (player1Stats != null) {
                statP1.text = player1Stats.javaClass.getMethod("get${statCategory.value}")
                    .invoke(player1Stats).toString()
            }

            if (player2Stats != null) {
                statP2.text = player2Stats.javaClass.getMethod("get${statCategory.value}")
                    .invoke(player2Stats).toString()
            }

            row.addView(statP1)
            row.addView(statLabel)
            row.addView(statP2)
            statsTable.addView(row)
        }
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
