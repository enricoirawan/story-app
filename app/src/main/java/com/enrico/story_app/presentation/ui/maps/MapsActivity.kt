package com.enrico.story_app.presentation.ui.maps

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.enrico.story_app.R
import com.enrico.story_app.data.ResultState
import com.enrico.story_app.data.remote.response.Story
import com.enrico.story_app.databinding.ActivityMapsBinding
import com.enrico.story_app.presentation.ui.ViewModelFactory
import com.enrico.story_app.utils.Constant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val boundsBuilder = LatLngBounds.Builder()

    private var token: String? = null

    private lateinit var factory: ViewModelFactory
    private val mapsViewModel: MapsViewModel by viewModels {
        factory
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        getMyLocation()
        setMapStyle()

        sharedPreferences = getSharedPreferences(Constant.preferencesName, Context.MODE_PRIVATE)
        token = sharedPreferences.getString(Constant.preferencesToken, "")

        token?.let {
            getStories(it)
        }
    }

    private fun getStories(token: String) {
        mapsViewModel.getStoriesForMaps(token).observe(this) { result ->
            if (result != null) {
                when(result){
                    is ResultState.Loading -> {
                        Snackbar.make(binding.root, R.string.loading, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ResourcesCompat.getColor(resources, R.color.colorPrimary, theme))
                            .show()
                    }
                    is ResultState.Success -> {
                        addStoriesMarker(result.data)
                    }
                    is ResultState.Error -> {
                        Snackbar.make(binding.root, result.error, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ResourcesCompat.getColor(resources, R.color.colorError, theme))
                            .setAction(R.string.retry) {
                                mapsViewModel.getStoriesForMaps(token)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun addStoriesMarker(storyList: List<Story>) {
        storyList.forEach{ story ->
            val latLng = LatLng(story.lat, story.lon)
            val addressName = getAddressName(story.lat, story.lon)
            mMap.addMarker(MarkerOptions().position(latLng).title(story.name).snippet(addressName))
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Snackbar.make(binding.root, R.string.styling_parsing_failed, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ResourcesCompat.getColor(resources, R.color.colorError, theme))
                    .show()
            }
        } catch (exception: Resources.NotFoundException) {
            Snackbar.make(binding.root, R.string.resource_not_found, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ResourcesCompat.getColor(resources, R.color.colorError, theme))
                .show()
        }
    }
}