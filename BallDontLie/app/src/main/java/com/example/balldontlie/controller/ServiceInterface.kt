package com.example.balldontlie.controller

import org.json.JSONObject

interface ServiceInterface {
    fun get(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit)
}