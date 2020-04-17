package com.example.balldontlie.controller

import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

/**
 * Implementation of Volley controller to make API requests.
 */
class ServiceVolley : ServiceInterface {
    val TAG: String = ServiceVolley::class.java.simpleName
    val basePath = "https://balldontlie.io/api/v1/"

    /**
     * Make a GET request to an API.
     * @param path: request path.
     * @param params: parameters to send in the body of the request.
     * NOTE - the BallDontLie API uses parameters in the path not the body, typically.
     * @param completionHandler: function to execute on successful response.
     */
    override fun get(
        path: String,
        params: JSONObject,
        completionHandler: (response: JSONObject?) -> Unit
    ) {
        val jsonObjReq = object : JsonObjectRequest(Method.POST, basePath + path, params,
            Response.Listener<JSONObject> { response ->
                Log.d(TAG, "/get request OK! Response: $response")
                completionHandler(response)
            },
            Response.ErrorListener { error ->
                VolleyLog.e(TAG, "/get request fail! Error: ${error.message}")
                completionHandler(null)
            }) {}
        BackendVolley.instance?.addToRequestQueue(jsonObjReq, TAG)
    }
}