package com.example.balldontlie

import android.animation.AnimatorInflater
import androidx.appcompat.app.AppCompatActivity
import android.animation.AnimatorSet
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.balldontlie.controller.APIController
import com.example.balldontlie.controller.ServiceVolley
import com.example.balldontlie.model.Player
import com.example.balldontlie.model.SelectedPlayers
import com.example.balldontlie.model.Stats
import com.example.balldontlie.schedule.getRegularSeason
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_compare.*
import kotlinx.android.synthetic.main.search_popup.*
import kotlinx.coroutines.*
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Use the [CompareFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompareFragment() : Fragment() {

    private lateinit var ctx: Context
    private lateinit var controller: APIController
    private lateinit var searchAdapter: ArrayAdapter<Player>
    private var displayedPlayers: MutableList<Player> = ArrayList()

    // Allow the user to select two players to compare for stats.
    private var selectedPlayers: SelectedPlayers = SelectedPlayers()

    private lateinit var myInflater: LayoutInflater
    private lateinit var searchDialog: AlertDialog
    private lateinit var vibrator: Vibrator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myInflater = inflater
        ctx = container!!.context
        vibrator = ctx.getSystemService(VIBRATOR_SERVICE) as Vibrator
        controller = APIController(ServiceVolley())
        return inflater.inflate(R.layout.fragment_compare, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchButton.setOnClickListener { showSearchDialog() }
    }

    /**
     * Get layout params for a list view.
     * Used to set the search list view height to not be too big.
     */
    private fun getNewHeightParam(numItems: Int, listView: ListView): ViewGroup.LayoutParams {
        val item: View = searchAdapter.getView(0, null, listView)
        item.measure(0, 0)
        val newParams: ViewGroup.LayoutParams = listView.layoutParams
        newParams.height = item.measuredHeight * numItems
        return newParams
    }

    private fun showSearchDialog() {
        val searchPopup: View = myInflater.inflate(R.layout.search_popup, null)

        val searchResultList = searchPopup.findViewById<ListView>(R.id.searchListView)
        val searchText = searchPopup.findViewById<EditText>(R.id.searchEditText)
        val player1Card: CardView = searchPopup.findViewById(R.id.player1Card)
        val player2Card: CardView = searchPopup.findViewById(R.id.player2Card)
        val player1Text: TextView = searchPopup.findViewById(R.id.player1Text)
        val player2Text: TextView = searchPopup.findViewById(R.id.player2Text)
        val clearButton: Button = searchPopup.findViewById(R.id.clearButton)
        val dismissButton: Button = searchPopup.findViewById(R.id.dismissButton)

        player1Text.text = ""
        player2Text.text = ""

        // Event handling for widgets
        searchAdapter = ArrayAdapter(
            ctx,
            android.R.layout.simple_list_item_1,
            displayedPlayers
        ).also { adapter -> searchResultList.adapter = adapter }


        val searchDelay: Long = 200
        searchText.afterTextChangedDebounce(searchDelay) { searchTerm ->
            controller.get("players?search=$searchTerm", JSONObject()) { response ->
                populateSearch(response)
                searchResultList.layoutParams =
                    getNewHeightParam(numItems = 3, listView = searchResultList)
            }
        }

        searchResultList.setOnItemClickListener { parent, view, position, id ->
            val vibrateLength: Long = 500
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    vibrateLength, VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
            val selectedPlayer: Player = displayedPlayers[position]
            val added: Boolean = selectedPlayers.addPlayer(selectedPlayer)
            if (!added) {
                Toast.makeText(
                    ctx,
                    "Clear players before adding more",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val names: List<String> = selectedPlayers.getPlayerNames()
            player1Text.text = names[0]
            player2Text.text = names[1]
        }

        clearButton.setOnClickListener() {
            // Animate the clear button to shake
            val animationSet: AnimatorSet =
                AnimatorInflater.loadAnimator(
                    ctx,
                    R.animator.shake
                ) as AnimatorSet
            animationSet.setTarget(it)
            animationSet.start()
            selectedPlayers.clearPlayers()
            player1Text.text = ""
            player2Text.text = ""
        }

        player1Card.setOnClickListener {
            if (selectedPlayers.player1 != null) {
                val intent: Intent = Intent(Intent.ACTION_WEB_SEARCH)
                intent.putExtra(
                    SearchManager.QUERY,
                    "${selectedPlayers.player1!!.first_name} ${selectedPlayers.player1!!.last_name}"
                )
                startActivity(intent)
            }
        }

        player2Card.setOnClickListener {
            if (selectedPlayers.player2 != null) {
                val intent: Intent = Intent(Intent.ACTION_WEB_SEARCH)
                intent.putExtra(
                    SearchManager.QUERY,
                    "${selectedPlayers.player2!!.first_name} ${selectedPlayers.player2!!.last_name}"
                )
                startActivity(intent)
            }
        }

        dismissButton.setOnClickListener {
            showPlayerStats()
        }

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(ctx)
        dialogBuilder.setOnDismissListener {
            // TODO - add the player stats to the table here
        }
        dialogBuilder.setView(searchPopup)
        searchDialog = dialogBuilder.create()
        searchDialog.show()
    }

    private fun EditText.afterTextChangedDebounce(delayMillis: Long, handler: (String) -> Unit) {
        var oldSearch = ""
        var debounceJob: Job? = null
        val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    val newSearch = s.toString()
                    debounceJob?.cancel()
                    if (newSearch != oldSearch) {
                        oldSearch = newSearch
                        debounceJob = uiScope.launch {
                            delay(delayMillis)
                            if (oldSearch == newSearch) {
                                handler(newSearch)
                            }
                        }
                    }
                }
            }

            override fun beforeTextChanged(
                cs: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                cs: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }
        })
    }

    /**
     * Called when the user clicks on the confirm button.
     * Take the selected players and display their stats in the stats table.
     */
    private fun showPlayerStats() {
        setPlayerStats()
        // Close the popup view
    }

    /**
     * Call the API for the selected player's current season stats.
     * Set the stats for the players.
     */
    private fun setPlayerStats() {
        val currentSeason = getRegularSeason()
        if (selectedPlayers.player1 != null) {
            controller.get(
                path = "season_averages?season=${currentSeason}&player_ids[]=${selectedPlayers.player1!!.id}",
                params = JSONObject()
            ) { response ->
                selectedPlayers.player1!!.stats = getStatsFromResponse(response)
                if (selectedPlayers.player2 == null) {
                    displayPlayerStats()
                }
            }
        }

        if (selectedPlayers.player2 != null) {
            controller.get(
                path = "season_averages?season=${currentSeason}&player_ids[]=${selectedPlayers.player2!!.id}",
                params = JSONObject()
            ) { response ->
                selectedPlayers.player2!!.stats = getStatsFromResponse(response)
                displayPlayerStats()
            }
        }
    }

    private fun displayPlayerStats() {
        val player1Stats = selectedPlayers.player1?.stats
        val player2Stats = selectedPlayers.player2?.stats
        Log.i("Stats", player1Stats.toString())
        Log.i("Stats", player2Stats.toString())

        // Quick points example
        // TODO - use reflection to loop here
        val row = TableRow(ctx)
        row.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val p1Pts = TextView(ctx)
        val p2Pts = TextView(ctx)
        val ptsLabel = TextView(ctx)

        p1Pts.text = ""
        p2Pts.text = ""
        ptsLabel.text = "Points"

        if (player1Stats != null) {
            p1Pts.text = player1Stats.pts.toString()
        }

        if (player2Stats != null) {
            p2Pts.text = player2Stats.pts.toString()
        }

        row.addView(p1Pts)
        row.addView(ptsLabel)
        row.addView(p2Pts)

        statsTable.addView(row)
    }

    /**
     * Populate the search list view with these players.
     */
    private fun populateSearch(response: JSONObject?) {
        displayedPlayers.clear()
        val data = JSONObject(response.toString()).getJSONArray("data")
        for (i in 0 until data.length()) {
            displayedPlayers.add(Gson().fromJson(data.getString(i), Player::class.java))
        }
        searchAdapter.notifyDataSetChanged()
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
