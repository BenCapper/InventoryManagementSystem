package org.wit.inventory.models

interface StockStore {
    fun findAll(): List<StockModel>
    fun create(stock: StockModel)
    fun update(stock: StockModel)
    fun delete(stock: StockModel)
    fun filterStock(stockName: String): List<StockModel>
    fun search(id: Long): List<StockModel>
}