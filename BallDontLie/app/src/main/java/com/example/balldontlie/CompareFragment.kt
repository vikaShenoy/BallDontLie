package com.example.balldontlie

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.balldontlie.controller.APIController
import com.example.balldontlie.controller.ServiceInterface
import com.example.balldontlie.controller.ServiceVolley
import com.example.balldontlie.model.Player
import kotlinx.android.synthetic.main.fragment_compare.*


/**
 * A simple [Fragment] subclass.
 * Use the [CompareFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompareFragment : Fragment() {

    private lateinit var ctx: Context
    private lateinit var controller: APIController
    private var displayedPlayers: List<Player> = ArrayList<Player>()
    private var selectedPlayers: List<Player> = ArrayList<Player>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            ctx = container.context
        }
        val service: ServiceInterface = ServiceVolley()
        controller = APIController(service)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compare, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearch()
    }

    /**
     * Initalise the adapters and views for searching for players.
     */
    private fun initSearch() {
        ArrayAdapter(
            ctx,
            android.R.layout.simple_list_item_1,
            displayedPlayers
        // TODO - Set an on item click listener for when they tap on a player
        // TODO - Add vibration here
        ).also { adapter -> searchListView.adapter = adapter }

        // TODO - add the rxtextview handling for searching to the search widget

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
