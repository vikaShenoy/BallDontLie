package com.example.balldontlie.model

class SelectedPlayers() {
    var player1: Player? = null
    var player2: Player? = null

    /**
     * Add the player to a free slot if there is one.
     * Return true if the player was added, false if not.
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
     * Return a two-element array containing the names of the selected players.
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
}