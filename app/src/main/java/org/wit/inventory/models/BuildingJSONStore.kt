package org.wit.inventory.models

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.AnkoLogger
import org.wit.inventory.helpers.exists
import org.wit.inventory.helpers.read
import org.wit.inventory.helpers.write
import java.util.*

const val JSON_BUILD_FILE = "buildings.json"
val gsonBuildingBuilder = GsonBuilder().setPrettyPrinting().create()
val buildingListType = object : TypeToken<java.util.ArrayList<BuildingModel>>() {}.type

fun generateRandomBuildId(): Long {
    return Random().nextLong()
}

class BuildingJSONStore : BuildingStore, AnkoLogger {

    val context: Context
    var buildings = mutableListOf<BuildingModel>()

    constructor (context: Context) {
        this.context = context
        if (exists(context, JSON_BUILD_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<BuildingModel> {
        return buildings
    }

    override fun create(building: BuildingModel) {
        building.id = generateRandomBuildId()
        buildings.add(building)
        serialize()
    }


    override fun update(building: BuildingModel) {
        val buildingsList = findAll() as ArrayList<BuildingModel>
        var foundBuilding: BuildingModel? = buildingsList.find { b -> b.id == building.id }
        if (foundBuilding != null) {
            foundBuilding.name = building.name
            foundBuilding.address = building.address
            foundBuilding.image = building.image
            foundBuilding.lat = building.lat
            foundBuilding.lng = building.lng
            foundBuilding.zoom = building.zoom
        }
        serialize()
    }

    override fun delete(building: BuildingModel) {
        buildings.remove(building)
        serialize()
    }

    override fun filterBuildings(buildingName: String): List<BuildingModel> {
        return buildings.filter { b -> b.name.toLowerCase().contains(buildingName.toLowerCase()) }
    }

    private fun serialize() {
        val jsonString = gsonBuildingBuilder.toJson(buildings, buildingListType)
        write(context, JSON_BUILD_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_BUILD_FILE)
        buildings = Gson().fromJson(jsonString, buildingListType)
    }
}