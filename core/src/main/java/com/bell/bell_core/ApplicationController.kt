package com.bell.bell_core

import android.content.Context

/************
 * Created by Leila Faraghi on 2019-03-27.
 ********/

interface ApplicationController {
    fun getTweetUrl() : String?
    fun getAuthUrl(): String?
    fun getConsumerKey():String?
    fun getConsumerKeySecret():String?
    fun getAppContext() : Context?
}