package com.bell.bell_core.tweet

import java.io.Serializable


/************
 * Created by Leila Faraghi on 2019-03-28.
 ********/

interface TweetContract {
    interface View {
        fun showTweets(tweets: List<Tweet>)
        fun showComplete()
        fun showError(message: String)
    }

    interface Presenter {
        fun loadTweets(keyword: String)
        fun didSelectedTweet(tweet: Tweet)
    }

    interface Router {
        fun showTweetDetail(tweet: Tweet)
    }

    class Token {
        var token_type: String? = null
        var access_token: String? = null
    }

    class Tweet(
        var created_at: String,
        var text: String,
        var geo: Geo,
        var user: User
    ) : Serializable {
    }

    class User(
        var screen_name: String,
        var location: String,
        var followers_count: Int
    ) : Serializable {

        fun printUser(): String {
            return "User name : $screen_name\n Number of followers: $followers_count"
        }
    }

    class Geo(
        var type: String,
        // var coordinates: List<Coordinate>
        var coordinates: List<Double>
    ) : Serializable {
    }

    class Coordinate(
        var lat: Double,
        var long: Double
    ) : Serializable {

    }

    class Status(var statuses: List<Tweet>) {
    }
}