package org.wit.inventory.models

interface BuildingStore {
    fun findAll(): List<BuildingModel>
    fun create(building: BuildingModel)
    fun update(building: BuildingModel)
    fun delete(building: BuildingModel)
    fun filterBuildings(buildingName: String): List<BuildingModel>
}