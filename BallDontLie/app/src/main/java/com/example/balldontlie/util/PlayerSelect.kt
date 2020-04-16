package com.example.balldontlie.util

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.balldontlie.R
import com.example.balldontlie.controller.APIController
import com.example.balldontlie.model.Player
import com.example.balldontlie.model.SelectedPlayers
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.json.JSONObject

class PlayerSelect : AppCompatActivity() {
    private var displayedPlayers: MutableList<Player> = ArrayList()
    var selectedPlayers: SelectedPlayers = SelectedPlayers()
    private lateinit var searchAdapter: ArrayAdapter<Player>

    fun createSearchDialog(
        ctx: Context,
        inflater: LayoutInflater,
        controller: APIController,
        onDismissCallback: () -> Unit
    ): AlertDialog {
        val searchPopup: View = inflater.inflate(R.layout.search_popup, null)

        val searchResultList = searchPopup.findViewById<ListView>(R.id.searchListView)
        val searchText = searchPopup.findViewById<EditText>(R.id.searchEditText)
        val player1Card: CardView = searchPopup.findViewById(R.id.player1Card)
        val player2Card: CardView = searchPopup.findViewById(R.id.player2Card)
        val player1Text: TextView = searchPopup.findViewById(R.id.player1Text)
        val player2Text: TextView = searchPopup.findViewById(R.id.player2Text)
        val clearButton: Button = searchPopup.findViewById(R.id.clearButton)
        val confirmButton: Button = searchPopup.findViewById(R.id.confirmButton)

        player1Text.text = ""
        player2Text.text = ""


        // Event handling for widgets on the popup view
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
                    getNewHeightParam(
                        searchAdapter = searchAdapter,
                        numItems = 3,
                        listView = searchResultList
                    )
            }
        }

        searchResultList.setOnItemClickListener { parent, view, position, id ->
            val vibrateLength: Long = 500
            val vibrator = ctx.getSystemService(VIBRATOR_SERVICE) as Vibrator
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

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(ctx)

        dialogBuilder.setView(searchPopup)
        val searchDialog = dialogBuilder.create()
        confirmButton.setOnClickListener {
            if (selectedPlayers.player1 == null) {
                Toast.makeText(ctx, "Select at least one player", Toast.LENGTH_SHORT).show()
            } else {
                onDismissCallback()
                searchDialog.dismiss()
            }
        }
        return searchDialog
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
 * Get layout params for a list view.
 * Used to set the search list view height to not be too big.
 */
private fun getNewHeightParam(
    searchAdapter: ArrayAdapter<Player>,
    numItems: Int,
    listView: ListView
): ViewGroup.LayoutParams {
    val item: View = searchAdapter.getView(0, null, listView)
    item.measure(0, 0)
    val newParams: ViewGroup.LayoutParams = listView.layoutParams
    newParams.height = item.measuredHeight * numItems
    return newParams
}
