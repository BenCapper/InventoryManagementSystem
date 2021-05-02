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
import kotlin.collections.ArrayList

const val JSON_STOCK_FILE = "stock.json"
val gsonStockBuilder = GsonBuilder().setPrettyPrinting().create()
val stockListType = object : TypeToken<java.util.ArrayList<StockModel>>() {}.type

fun generateRandomStockId(): Long {
    return Random().nextLong()
}

class StockJSONStore : StockStore, AnkoLogger {

    val context: Context
    var stocks = mutableListOf<StockModel>()

    constructor (context: Context) {
        this.context = context
        if (exists(context, JSON_STOCK_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<StockModel> {
        return stocks
    }


    override fun create(stock: StockModel) {
        stock.id = generateRandomStockId()
        stocks.add(stock)
        serialize()
    }


    override fun update(stock: StockModel) {
        val stocksList = findAll() as ArrayList<StockModel>
        var foundStock: StockModel? = stocksList.find { s -> s.id == stock.id }
        if (foundStock != null) {
            foundStock.name = stock.name
            foundStock.branch = stock.branch
            foundStock.dept = stock.dept
            foundStock.weight = stock.weight
            foundStock.price = stock.price
            foundStock.image = stock.image
            foundStock.inStock = stock.inStock
        }
        serialize()
    }

    override fun filterStock(stockName: String): List<StockModel> {
        return stocks.filter { s -> s.name.toLowerCase().contains(stockName.toLowerCase()) }
    }

    override fun search(id: Long): List<StockModel> {
        TODO("Not yet implemented")
    }

    override fun delete(stock: StockModel) {
        stocks.remove(stock)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonStockBuilder.toJson(stocks, stockListType)
        write(context, JSON_STOCK_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_STOCK_FILE)
        stocks = Gson().fromJson(jsonString, stockListType)
    }
}