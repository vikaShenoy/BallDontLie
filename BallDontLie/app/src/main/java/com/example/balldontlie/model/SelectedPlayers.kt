package com.example.balldontlie.model

/**
 * Class to represent players the user has chosen in the search popup.
 * Implementation hard codes for two players currently, this can be extended in the future.
 */
class SelectedPlayers() {
    var player1: Player? = null
    var player2: Player? = null

    /**
     * Add the player to a free slot if there is one.
     * @return true if the player was added, false if not.
     */
    fun addPlayer(player: Player): Boolean {
        if (player1 == null) {
            player1 = player
        } else if (player2 == null) {
            player2 = player
        } else {
            return false
        }
        return true
    }

    fun clearPlayers() {
        player1 = null
        player2 = null
    }

    /**
     * @return a two-element array containing the names of the selected players.
     * Empty strings are used if the slot is empty.
     */
    fun getPlayerNames(): List<String> {
        val names = ArrayList<String>()
        if (player1 != null) {
            names.add(player1!!.last_name)
        } else {
            names.add("")
        }

        if (player2 != null) {
            names.add(player2!!.last_name)
        } else {
            names.add("")
        }
        return names
    }

    /**
     * @return true if there is at least one player selected.
     */
    fun playerPresent(): Boolean {
        if (player1 != null || player2 != null) {
            return true
        }
        return false
    }
}