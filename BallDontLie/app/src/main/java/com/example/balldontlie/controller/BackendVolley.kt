package com.example.balldontlie.controller

import android.app.Application
import android.text.TextUtils
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Implement the Volley requests for talking to an API.
 */
class BackendVolley : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    private val requestQueue: RequestQueue? = null
        get() {
            if (field == null) {
                return Volley.newRequestQueue(applicationContext)
            }
            return field
        }

    /**
     * Add a request to the queue with tag.
     * @param request: request object to add to the queue
     * @param tag: request tag
     */
    fun <T> addToRequestQueue(request: Request<T>, tag: String) {
        request.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        requestQueue?.add(request)
    }

    /**
     * Add a request to the queue with no tag.
     * @param request: request object to add to the queue
     */
    fun <T> addToRequestQueue(request: Request<T>) {
        request.tag =
            TAG
        requestQueue?.add(request)
    }

    /**
     * Stop the current requests in the queue from being executed.
     * @param tag: requests with this tag are cancelled.
     */
    fun cancelPendingRequests(tag: Any) {
        if (requestQueue != null) {
            requestQueue!!.cancelAll(tag)
        }
    }

    companion object {
        private val TAG = BackendVolley::class.java.simpleName

        @get:Synchronized
        var instance: BackendVolley? = null
            private set
    }
}