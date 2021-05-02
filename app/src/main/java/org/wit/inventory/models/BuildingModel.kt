package org.wit.inventory.models
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BuildingModel(var id: Long = 0,
                         var name: String = "",
                         var address: String = "",
                         var image: String = "",
                         var lat: Double = 0.0,
                         var lng: Double = 0.0,
                         var zoom: Float = 0f) : Parcelable

@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable

