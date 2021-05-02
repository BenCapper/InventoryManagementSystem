package org.wit.inventory.main

import android.app.Application
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.inventory.models.*


class MainApp : Application(), AnkoLogger {
    //lateinit var buildingsJson: BuildingStore
    //lateinit var stockJson: StockStore
    lateinit var builds: BuildingStore
    lateinit var stocks: StockStore


    override fun onCreate() {
        super.onCreate()
        //buildingsJson = BuildingJSONStore(applicationContext)
        //stockJson = StockJSONStore(applicationContext)
        builds = FirebaseBuildings()
        stocks = FirebaseStock()
        info("Inventory started")
    }
}