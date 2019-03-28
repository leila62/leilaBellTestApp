package com.bell.testapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.bell.ui.map.MapViewActivity
import com.twitter.sdk.android.core.*
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Twitter login
        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(TwitterAuthConfig(getString(R.string.consumer_key), getString(R.string.consumer_secret)))
            .debug(true)
            .build()
        Twitter.initialize(config)
        setContentView(R.layout.activity_login)

        //Login to twitter and get user info then  show by toast
        this.loginButton.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                val session = TwitterCore.getInstance().sessionManager.activeSession
                Toast.makeText(this@LoginActivity, "Welcome" + session.userName, Toast.LENGTH_LONG).show()
            }

            override fun failure(exception: TwitterException?) {
                Toast.makeText(this@LoginActivity, exception?.message, Toast.LENGTH_LONG).show()
            }
        }

        this.loadButton.setOnClickListener {
            navigateToNextScreen()
        }
    }


    private fun navigateToNextScreen() {
        val intent = Intent(this@LoginActivity, MapViewActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        this.loginButton.onActivityResult(requestCode, resultCode, data)
    }
}
