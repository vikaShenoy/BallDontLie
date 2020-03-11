package com.example.balldontlie.controller

import org.json.JSONObject

class APIController constructor(serviceInjection: ServiceInterface) : ServiceInterface {
    private val service: ServiceInterface = serviceInjection

    override fun get(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit) {
        service.get(path, params, completionHandler)
    }
}