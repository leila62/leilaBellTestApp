package com.bell.bell_core.tweet

import android.util.Base64
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bell.bell_core.ApplicationController
import com.google.gson.Gson
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

/************
 * Created by Leila Faraghi on 2019-03-28.
 ********/
class TweetPresenter : TweetContract.Presenter {

    companion object Constants {
        const val GRANT_TYPE = "grant_type=client_credentials"
        const val CONTENT_TYPE = "Content-Type"
        const val CONTENT_TYPE_TEXT = "application/x-www-form-urlencoded;charset=UTF-8"
        const val AUTHORIZATION = "Authorization"
        const val BEARER = "Bearer "
        const val BASIC = "Basic "
    }

    var mView: TweetContract.View? = null
    var mRouter: TweetContract.Router? = null
    var applicationController: ApplicationController? = null


    constructor(
        mView: TweetContract.View,
        //mRuoter: TweetContract.Router,
        applicationController: ApplicationController
    ) {
        this.mView = mView
        //this.mRouter = mRouter
        this.applicationController = applicationController
    }

    /****
     *
     * Load tweets by Token by Volley
     * First call oAuth endpoint by consumer and consumer secret key to get bearer token
     * then call search endpoint to get twitter statuses list
     *
     * ****/

    override fun loadTweets(keyword: String) {
        val queue = Volley.newRequestQueue(applicationController!!.getAppContext())

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, applicationController!!.getAuthUrl(), null,
            Response.Listener { response ->

                val auth: TweetContract.Token = parseToken(response.toString())!!
                if (auth != null && auth.token_type!!.equals(BEARER.toLowerCase().replace(" ", ""))) {
                    //get tweets by token
                    retrieveTweets(auth.access_token!!, keyword)
                }
            },
            Response.ErrorListener { error ->
                mView!!.showError(error.message!!)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                var consumerKey: String? = null
                var consumerKeySecret: String? = null
                try {
                    consumerKey = URLEncoder.encode(applicationController!!.getConsumerKey(), "UTF-8")
                    consumerKeySecret = URLEncoder.encode(applicationController!!.getConsumerKeySecret(), "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                // add api call headers and params
                val credentials = "$consumerKey:$consumerKeySecret"
                val auth = BASIC + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
                headers[CONTENT_TYPE] = CONTENT_TYPE_TEXT
                headers[AUTHORIZATION] = auth
                return headers
            }

            override fun getBody(): ByteArray {
                return GRANT_TYPE.toByteArray()
            }
        }
        queue.add(jsonObjectRequest)
    }


    fun retrieveTweets(token: String, keyword: String) {
        val queue = Volley.newRequestQueue(applicationController!!.getAppContext())
        //add default search query if user is not searching
        val result = if (keyword.isNotEmpty()) keyword else "montreal&amp;geocode=45.525387,-73.651188,1mi"

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, applicationController!!.getTweetUrl()+result, null,
                Response.Listener { response ->
                    val tweets = parseTweet(response.toString())
                    mView!!.showTweets(tweets!!.statuses)
                },
                Response.ErrorListener { error ->
                    mView!!.showError(error.message!!)
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers[AUTHORIZATION] = BEARER + token
                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }


    // convert a JSON Auth object into an Token object
    private fun parseToken(token: String?): TweetContract.Token? {
        var auth: TweetContract.Token? = null
        if (token != null && token.length > 0) {
            try {
                val gson = Gson()
                auth = gson.fromJson<TweetContract.Token>(token, TweetContract.Token::class.java)
            } catch (ex: IllegalStateException) {
                Log.e("error", ex.message)
            }
        }
        return auth
    }


    // convert a JSON Tweet object into an Tweet object
    private fun parseTweet(tweet: String?): TweetContract.Status? {
        var status: TweetContract.Status? = null

        if (tweet != null && tweet.isNotEmpty()) {
            try {
                val gson = Gson()
                status = gson.fromJson<TweetContract.Status>(tweet, TweetContract.Status::class.java!!)
            } catch (ex: IllegalStateException) {
                mView!!.showError(ex.message!!)
            }
        }
        return status
    }


    override fun didSelectedTweet(tweet: TweetContract.Tweet) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}