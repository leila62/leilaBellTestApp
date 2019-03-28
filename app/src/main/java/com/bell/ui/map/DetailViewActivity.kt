package com.bell.ui.map

import android.app.Activity
import android.os.Bundle
import com.bell.bell_core.tweet.TweetContract
import com.bell.testapp.R
import com.bell.ui.map.MapViewActivity.Constants.TWEET_KEY
import kotlinx.android.synthetic.main.activity_detail_view.*

/************
 * Created by Leila Faraghi on 2019-03-28.
 ********/

class DetailViewActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)

        var tweet = intent.getSerializableExtra(TWEET_KEY) as TweetContract.Tweet

       // Populate data
        this.titleTextView.text = tweet.text
        this.descriptionTextView.text = tweet.user.printUser()
    }
}