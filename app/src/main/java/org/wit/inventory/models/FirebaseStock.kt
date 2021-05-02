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


class FirebaseStock : StockStore, AnkoLogger {

    var stock = mutableListOf<StockModel>()
    private val db = FirebaseDatabase.getInstance().reference.child("Stock")


    override fun findAll(): MutableList<StockModel> {
        return stock
    }


    fun generateRandomStockId(): Long {
        return Random().nextLong()
    }
    override fun create(stock: StockModel) {
        db.child(stock.id.toString()).setValue(stock)
    }


    override fun update(stock: StockModel) {
        db.child(stock.id.toString()).setValue(stock)

    }

    override fun delete(stock: StockModel) {
        db.child(stock.id.toString()).removeValue()

    }


    override fun filterStock(stockName: String): List<StockModel> {
        return stock.filter { b -> b.name.toLowerCase().contains(stockName.toLowerCase()) }
    }

    override fun search(id: Long): List<StockModel> {
        return stock.filter { s -> s.branch == id }
    }
}