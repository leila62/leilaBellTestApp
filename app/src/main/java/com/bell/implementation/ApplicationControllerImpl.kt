package com.bell.ui.implementation

import android.content.Context
import com.bell.bell_core.ApplicationController
import com.bell.testapp.R

/************
 * Created by Leila Faraghi on 2019-03-27.
 ********/


class ApplicationControllerImpl : ApplicationController {

    /**
     * Get different strings and other app properties
     * Values are loaded during app build
     */

    var context: Context? = null

     constructor(context: Context) {
        this.context = context
    }

    override fun getAuthUrl(): String? {
        if (context != null) {
            return context!!.getString(R.string.auth_url)
        } else return null
    }

    override fun getConsumerKey(): String? {
        if (context != null) {
            return context!!.getString(R.string.consumer_key)
        } else return null
    }

    override fun getConsumerKeySecret(): String? {
        if (context != null) {
            return context!!.getString(R.string.consumer_secret)
        } else return null
    }


    override fun getTweetUrl(): String? {
        if (context != null) {
            return context!!.getString(R.string.tweet_url)
        } else return null
    }

    override fun getAppContext(): Context? {
        if (context != null) {
            return context!!
        } else return null
    }
}