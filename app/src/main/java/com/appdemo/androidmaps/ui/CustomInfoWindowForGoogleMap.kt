package com.appdemo.androidmaps.ui

import android.content.Context
import android.view.View
import com.appdemo.androidmaps.MapsActivity
import com.appdemo.androidmaps.databinding.CustomInfoWindowBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowForGoogleMap(context: Context) : GoogleMap.InfoWindowAdapter {
    private val layoutInflater = (context as MapsActivity).layoutInflater
    private var window = CustomInfoWindowBinding.inflate(layoutInflater, null, false)

    private fun renderWindowText(marker: Marker, view: CustomInfoWindowBinding) {
        val tvTitle = view.title
        val tvSnippet = view.snippet
        tvTitle.text = marker.title
        tvSnippet.text = marker.snippet
    }

    override fun getInfoContents(marker: Marker): View {
        renderWindowText(marker, window)
        return window.root
    }

    override fun getInfoWindow(marker: Marker): View {
        renderWindowText(marker, window)
        return window.root
    }
}