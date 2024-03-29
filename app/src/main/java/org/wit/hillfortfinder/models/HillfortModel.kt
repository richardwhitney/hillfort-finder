package org.wit.hillfortfinder.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HillfortModel(var id: Long = 0,
                         var title: String = "",
                         var description: String = "",
                         var images: ArrayList<String> = arrayListOf(),
                         var lat: Double = 0.0,
                         var lng: Double = 0.0,
                         var zoom: Float = 0f,
                         var visited: Boolean = false,
                         var userId: Long = 0,
                         var additionalNotes: String = "",
                         var dateVisited: String = "") : Parcelable

@Parcelize
data class Location(var lat :Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable