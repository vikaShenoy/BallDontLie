package com.example.balldontlie

import android.app.AlertDialog
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.transition.Transition
import com.example.balldontlie.controller.APIController
import com.example.balldontlie.controller.ServiceInterface
import com.example.balldontlie.controller.ServiceVolley
import com.example.balldontlie.model.Player
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
class CompareFragment : Fragment() {

    private lateinit var ctx: Context
    private lateinit var controller: APIController
    private lateinit var searchAdapter: ArrayAdapter<Player>
    private var displayedPlayers: MutableList<Player> = ArrayList<Player>()
    private var selectedPlayers: MutableList<Player> = ArrayList<Player>()

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
        val service: ServiceInterface = ServiceVolley()
        controller = APIController(service)
        return inflater.inflate(R.layout.fragment_compare, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchButton.setOnClickListener { showSearchDialog() }
    }

    private fun showSearchDialog() {
        val searchPopup: View = myInflater.inflate(R.layout.search_popup, null)
        val searchResultList = searchPopup.findViewById<ListView>(R.id.searchListView)
        val searchText = searchPopup.findViewById<EditText>(R.id.searchEditText)
        val player1Card: CardView = searchPopup.findViewById<CardView>(R.id.player1Card)
        val player2Card: CardView = searchPopup.findViewById<CardView>(R.id.player2Card)

        // Event handling for widgets
        searchAdapter = ArrayAdapter(
            ctx,
            android.R.layout.simple_list_item_1,
            displayedPlayers
        ).also { adapter -> searchResultList.adapter = adapter }

        val searchDelay: Long = 500
        searchText.afterTextChangedDebounce(searchDelay) { searchTerm ->
            controller.get("players?search=$searchTerm", JSONObject()) { response ->
                populateSearch(response)
            }
        }

        searchResultList.setOnItemClickListener { parent, view, position, id ->
            val vibrateLength: Long = 500
            vibrator.vibrate(VibrationEffect.createOneShot(
                vibrateLength, VibrationEffect.DEFAULT_AMPLITUDE))
            val selectedPlayer: Player = displayedPlayers[position]
        }

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(ctx)
        dialogBuilder.setOnDismissListener {
            Toast.makeText(ctx, "Dismissed", Toast.LENGTH_SHORT).show()
        }

        dialogBuilder.setView(searchPopup)
        searchDialog = dialogBuilder.create()
        //alertDialog.window!!.getAttributes().windowAnimations = R.style.PauseDialogAnimation
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
