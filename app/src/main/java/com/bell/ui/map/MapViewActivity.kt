package com.bell.ui.map

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.bell.bell_core.tweet.TweetContract
import com.bell.bell_core.tweet.TweetPresenter
import com.bell.testapp.R
import com.bell.ui.implementation.ApplicationControllerImpl
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map_view.*
import java.io.Serializable
import java.util.*


/************
 * Created by Leila Faraghi on 2019-03-27.
 ********/

class MapViewActivity : FragmentActivity(), TweetContract.View, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    companion object Constants {
        const val TWEET_KEY = "tweet_key"
    }

    var presenter: TweetPresenter? = null
    var applicationController: ApplicationControllerImpl? = null
    var searchKeyword = String()

    //MAP
    var map: GoogleMap? = null


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)

        applicationController = ApplicationControllerImpl(this)
        presenter = TweetPresenter(this, applicationController!!)
        presenter!!.loadTweets("")
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        //setup Listeners
        this.searchImageButton.setOnClickListener {
            if (searchKeyword.isNotEmpty()) {
                presenter!!.loadTweets(searchKeyword)
                this.searchImageButton.setBackgroundResource(R.drawable.close);
            }
            this.searchImageButton.setBackgroundResource(R.drawable.close);
        }


        this.searchImageButton.setOnClickListener {
            if (searchKeyword.isNotEmpty()) {
                searchKeyword = ""
                this.searchImageButton.setBackgroundResource(R.drawable.search);

            }
        }


        this.searchEditText.setOnEditorActionListener() { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_GO
                || actionId == EditorInfo.IME_ACTION_SEARCH
            ) {
                closeKeyboard()
                presenter!!.loadTweets(searchKeyword)
                true
            } else {
                false
            }
        }

        this.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(str: Editable?) {
                searchKeyword = str.toString()
                // presenter!!.loadTweets(str.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (searchKeyword.isNotEmpty()) {
                    searchImageButton.setBackgroundResource(R.drawable.close);
                } else {
                    searchImageButton.setBackgroundResource(R.drawable.search);

                }
            }

        })
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap;
        map!!.setOnInfoWindowClickListener(object  : GoogleMap.OnInfoWindowClickListener{
            override fun onInfoWindowClick(marker: Marker?) {
                navigateToScreen(marker!!.tag as TweetContract.Tweet)
            }
        })
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        return true
    }


    override fun showTweets(tweets: List<TweetContract.Tweet>) {
        addMarkers(tweets)
    }

    override fun showComplete() {
    }

    override fun showError(message: String) {
        Toast.makeText(this@MapViewActivity, message, Toast.LENGTH_LONG).show()
    }

    protected fun addMarkers(items: List<TweetContract.Tweet>) {
        val positions = ArrayList<LatLng>()

        for (tweet in items) {
            if (tweet.geo != null) {
                positions.add(LatLng(tweet.geo.coordinates.get(0), tweet.geo.coordinates.get(1)))
                val markerOptions = MarkerOptions()
                    .position(LatLng(tweet.geo.coordinates.get(0), tweet.geo.coordinates.get(1)))
                    .anchor(0.5f, 0.5f)
                    .title(tweet.text)
                 var marker = map!!.addMarker(markerOptions)
                marker.tag = tweet
            }
            map!!.moveCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        positions.get(0).latitude,
                        positions.get(0).longitude
                    )
                )
            )

            val cameraPosition = CameraPosition.Builder()
                .target(
                    LatLng(
                        positions.get(0).latitude,
                        positions.get(0).longitude
                    )
                )
                .zoom(10f)                   // Sets the zoom
                .bearing(90f)                // Sets the orientation of the camera to east
                .tilt(30f)                   // Sets the tilt of the camera to 30 degrees
                .build()                   // Creates a CameraPosition from the builder
            map!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }


    // Helpers
    fun closeKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = currentFocus
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    private fun navigateToScreen(tweet: TweetContract.Tweet) {
        val intent = Intent(this@MapViewActivity, DetailViewActivity::class.java)
        intent.putExtra(TWEET_KEY,  tweet as Serializable)
        startActivity(intent)
    }
}

