package org.wit.inventory.models

import android.content.Context
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.AnkoLogger
import org.wit.inventory.helpers.exists
import org.wit.inventory.helpers.read
import org.wit.inventory.helpers.write
import java.util.*
import java.util.logging.Logger


class FirebaseBuildings : BuildingStore, AnkoLogger {

    var buildings = mutableListOf<BuildingModel>()
    private val db = FirebaseDatabase.getInstance().reference.child("Building")


    override fun findAll(): MutableList<BuildingModel> {
        return buildings
    }


    fun generateRandomBuildId(): Long {
        return Random().nextLong()
    }
    override fun create(building: BuildingModel) {
        db.child(building.id.toString()).setValue(building)
    }


    override fun update(building: BuildingModel) {
        db.child(building.id.toString()).setValue(building)

    }

    override fun delete(building: BuildingModel) {
        db.child(building.id.toString()).removeValue()
    }

    override fun filterBuildings(buildingName: String): List<BuildingModel> {
        return buildings.filter { b -> b.name.toLowerCase().contains(buildingName.toLowerCase()) }
    }

}