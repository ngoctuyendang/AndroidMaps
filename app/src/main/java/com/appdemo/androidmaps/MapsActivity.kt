package com.appdemo.androidmaps

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.appdemo.androidmaps.databinding.ActivityMapsBinding
import com.appdemo.androidmaps.databinding.SearchBottomSheetDialogBinding
import com.appdemo.androidmaps.models.PlaceNote
import com.appdemo.androidmaps.ui.CustomInfoWindowForGoogleMap
import com.appdemo.androidmaps.ui.EditMarker
import com.appdemo.androidmaps.ui.SearchResultAdapter
import com.appdemo.androidmaps.viewmodels.MapsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.floor

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel: MapsViewModel by lazy {
        ViewModelProvider(this)[MapsViewModel::class.java]
    }

    private val searchResultAdapter by lazy {
        SearchResultAdapter { lat, long ->
            // Move to the position that user clicked on the result list
            val latLng = LatLng(lat, long)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    private lateinit var currentLocation: Location
    private var currentPositionMarker: Marker? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    private val GPS_CHECK = 112
    private lateinit var manager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check Location service is turned on
        manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        } else {
            getCurrentLocation()
        }
    }

    private fun getAllPlaceNotes() {
        viewModel.getNotes()
        viewModel.listMarker.observe(this) {
            val markerOps = MarkerOptions()
            val currentPosListNote = ArrayList<PlaceNote>()
            it.forEach { note ->
                val location = LatLng(note.lat, note.long)
                if (floor(note.lat * 10000) / 10000
                    == floor(currentLocation.latitude * 10000) / 10000
                ) {
                    // In case, notes of current location
                    currentPosListNote.add(note)
                } else {
                    // In another case
                    markerOps
                        .position(location)
                        .title("Notes")
                        .snippet(note.note)
                    mMap.addMarker(markerOps)
                }
            }

            var noteMsg = ""
            currentPosListNote.forEachIndexed { index, note ->
                noteMsg = noteMsg.plus(note.userName).plus(": ").plus(note.note)
                if (index != currentPosListNote.size - 1) noteMsg = noteMsg.plus("\n")
            }

            mMap.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(this))
            currentPositionMarker?.snippet = noteMsg
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GPS_CHECK && manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getCurrentLocation()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_CHECK)
            }.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    @SuppressLint("VisibleForTests")
    private fun getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Check location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                permissionCode
            )
            return
        }

        /* Have to request location update from FusedLocationProviderClient
        Then we can get the last location from FusedLocationProviderClient,
        it wouldn't be null in case user just turn on gps service */

        val mLocationRequest: LocationRequest = LocationRequest.create()
        mLocationRequest.interval = 60000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                //TODO - do not use now
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            null
        )

        fusedLocationProviderClient
            .lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }.addOnFailureListener {
                // When error should be show error message - do not impl now
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Update current location when LOCATION permission is allow
                getCurrentLocation()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title("Current location")

        currentPositionMarker = mMap.addMarker(markerOptions)
        currentPositionMarker?.showInfoWindow()

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

        setupEvent()
        getAllPlaceNotes()
    }

    private fun setupEvent() {
        // Add note to Firebase - only for current location
        mMap.setOnInfoWindowClickListener { marker ->
            if (marker.position.latitude != currentLocation.latitude
                || marker.position.longitude != currentLocation.longitude
            ) {
                return@setOnInfoWindowClickListener
            }

            val editMarker = EditMarker { name, note ->
                val placeNote = PlaceNote(
                    marker.position.latitude,
                    marker.position.longitude,
                    name,
                    note
                )
                viewModel.addDataToFirebase(placeNote)
            }
            editMarker.show(supportFragmentManager, "dialog")
        }

        // Search note with keyword
        binding.searchBtn.setOnClickListener {
            searchNote()
        }
    }

    private fun searchNote() {
        /* Show bottom sheet -> show list result -> click
        on item of result -> move map to this position */

        val dialog = BottomSheetDialog(this, R.style.DialogStyle)
        val view =
            SearchBottomSheetDialogBinding.inflate(layoutInflater, null, false)
        dialog.setCancelable(true)
        dialog.setContentView(view.root)
        dialog.show()

        dialog.setOnDismissListener {
            viewModel.clearData()
        }

        view.searchKeyword.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    // Gone error msg when user typing
                    view.keywordErrorMsg.visibility = View.GONE
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(p0: Editable?) {}
            }
        )

        view.searchKeyword.requestFocus()
        view.searchBtn.setOnClickListener {
            performSearch(view, it)
        }

        view.searchKeyword.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                textView?.let { performSearch(view, it) }
            }
            false
        }

        viewModel.searchListResult.observe(this) {
            view.searchResultError.visibility = View.GONE
            view.searchResult.visibility = View.VISIBLE
            view.searchResult.adapter = searchResultAdapter
            searchResultAdapter.submitList(it)
        }

        viewModel.searchError.observe(this) {
            view.searchResult.visibility = View.INVISIBLE
            view.searchResultError.visibility = View.VISIBLE
            view.searchResultError.text = it
        }
    }

    private fun performSearch(
        view: SearchBottomSheetDialogBinding,
        it: View,
    ) {
        val keyword = view.searchKeyword.text
        if (keyword.isNotEmpty()) {
            view.searchKeyword.clearFocus()
            viewModel.searchNote(keyword.toString())
            // Hide keyboard
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        } else {
            // Show error msg when user search without keyword
            view.keywordErrorMsg.visibility = View.VISIBLE
            viewModel.clearData()
        }
    }
}