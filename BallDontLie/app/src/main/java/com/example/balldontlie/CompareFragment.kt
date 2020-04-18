package com.example.balldontlie

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.example.balldontlie.model.Player
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

        val webSearch: (Intent) -> Unit = { startActivity(it) }

        val searchDialog = playerSelect.createSearchDialog(
            ctx, viewInflater, controller, webSearch
        ) {
            val selectedPlayers = playerSelect.selectedPlayers
            displayStatsInTable(selectedPlayers)
            //showPlayerStats()
        }
        searchDialog.show()
    }


    /**
     * Call the API for the selected player's current season stats.
     * Set the stats for the players.
     */
    private fun showPlayerStats() {
        //displayStatsInTable()
//        val currentSeason = getRegularSeason()
//        if (selectedPlayers.player1 != null) {
//            controller.get(
//                path = "season_averages?season=${currentSeason}&player_ids[]=${selectedPlayers.player1!!.id}",
//                params = JSONObject()
//            ) { response ->
//                selectedPlayers.player1!!.seasonStats =
//                    getSeasonStatsFromResponse(response)
//                if (selectedPlayers.player2 == null) {
//                    displayStatsInTable()
//                }
//            }
//        }
//
//        if (selectedPlayers.player2 != null) {
//            controller.get(
//                path = "season_averages?season=${currentSeason}&player_ids[]=${selectedPlayers.player2!!.id}",
//                params = JSONObject()
//            ) { response ->
//                selectedPlayers.player2!!.seasonStats =
//                    getSeasonStatsFromResponse(response)
//                displayStatsInTable()
//            }
//        }
    }

    /**
     * Construct the table rows which display the selected players stats.
     */
    private fun displayStatsInTable(selectedPlayers: SelectedPlayers) {
        val player1: Player? = selectedPlayers.player1
        val player2: Player? = selectedPlayers.player2

        // Header row
        statsTable.addView(
            createTableRow(
                player1?.last_name ?: "",
                "",
                player2?.last_name ?: "",
                0
            )
        )

        for (statCategory in statCategories) {
            val leftStat = if (player1 != null) player1.seasonStats?.javaClass?.getMethod(
                "get${statCategory.value}"
            )?.invoke(player1.seasonStats).toString() else ""

            val rightStat = if (player2 != null) player2.seasonStats?.javaClass?.getMethod(
                "get${statCategory.value}"
            )?.invoke(player2.seasonStats).toString() else ""

            var highlight = 0
            if (leftStat.isNotEmpty() && rightStat.isNotEmpty() && leftStat > rightStat) {
                highlight = 1
            } else if (leftStat.isNotEmpty() && rightStat.isNotEmpty() && leftStat < rightStat) {
                highlight = 2
            }

            statsTable.addView(createTableRow(leftStat, statCategory.key, rightStat, highlight))
        }
    }

    /**
     * Create a new table row.
     * @param textLeft: text to appear in the left cell.
     * @param textCenter: text to appear in the center cell.
     * @param textRight: text to appear in the right cell.
     * @param highlight: 0 for no highlight, 1 if the left stat is higher, 2 if the right stat is.
     * @return TableRow View to be added to a TableLayout.
     */
    private fun createTableRow(
        textLeft: String,
        textCenter: String,
        textRight: String,
        highlight: Int
    ): View {
        val row = viewInflater.inflate(R.layout.table_row, statsTable, false)
        row.leftColumn.text = textLeft
        row.centerColumn.text = textCenter
        row.rightColumn.text = textRight

        if (highlight == 1) {
            row.leftColumn.setTextColor(resources.getColor(R.color.colorAccent))
        } else if (highlight == 2) {
            row.rightColumn.setTextColor(resources.getColor(R.color.colorAccent))
        }

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
