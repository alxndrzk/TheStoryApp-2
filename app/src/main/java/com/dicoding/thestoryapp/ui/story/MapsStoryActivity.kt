package com.dicoding.thestoryapp.ui.story

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.thestoryapp.R
import com.dicoding.thestoryapp.databinding.ActivityMapsStoryBinding
import com.dicoding.thestoryapp.ui.story.viewmodel.StoryViewModel
import com.dicoding.thestoryapp.util.Result
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap
    private lateinit var viewbinding: ActivityMapsStoryBinding
    private val mapStoryViewModel: StoryViewModel by viewModels()
    private val mapBoundBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewbinding = ActivityMapsStoryBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        hideActionBar()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        gMap.uiSettings.isZoomControlsEnabled = true
        gMap.uiSettings.isIndoorLevelPickerEnabled = true
        gMap.uiSettings.isCompassEnabled = true
        gMap.uiSettings.isMapToolbarEnabled = true
        getDataStoryLocation()
    }

    private fun hideActionBar() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun getDataStoryLocation() {

        mapStoryViewModel.getListStoryLocation().observe(this) { responseResult ->
            when(responseResult) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    responseResult.data?.listStory?.forEach {
                        addMarker(LatLng(it.lat as Double, it.lon as Double), it.name as String)
                    }

                    val bounds: LatLngBounds = mapBoundBuilder.build()
                    gMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            300
                        )
                    )
                }
                else -> {
                    Toast.makeText(this@MapsStoryActivity, responseResult.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun addMarker(latLon: LatLng, creator: String) {
        gMap.addMarker(
            MarkerOptions()
                .position(latLon)
                .title(creator)
        )
        mapBoundBuilder.include(latLon)
    }

}