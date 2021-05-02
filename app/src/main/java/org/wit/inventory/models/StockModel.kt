package org.wit.inventory.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StockModel(var id: Long = 0,
                      var name: String = "",
                      var branch: Long = 0,
                      var dept: String = "",
                      var weight: String = "",
                      var price: Double = 0.00,
                      var inStock: Long = 0,
                      var image: String = "",) : Parcelable