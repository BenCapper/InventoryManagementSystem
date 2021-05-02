package org.wit.inventory.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_building_list.recyclerView
import kotlinx.android.synthetic.main.activity_building_list.toolbar
import kotlinx.android.synthetic.main.activity_stock_list.*
import org.jetbrains.anko.intentFor
import org.wit.inventory.R
import org.wit.inventory.main.MainApp
import org.wit.inventory.models.BuildingModel
import org.wit.inventory.models.StockModel


class StockListActivity : AppCompatActivity(), StockListener {

    lateinit var app: MainApp
    private lateinit var stockList: MutableList<StockModel>
    private lateinit var foundList: MutableList<StockModel>
    private val db = FirebaseDatabase.getInstance().reference.child("Stock")
    private lateinit var branchStock: BuildingModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_list)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        getStockData()
        app = application as MainApp
        branchStock = intent.extras?.getParcelable<BuildingModel>("branchName")!!

        toolbar.title = branchStock.name + " " + "Stock"
        setSupportActionBar(toolbar)

        //https://stackoverflow.com/questions/55949305/how-to-properly-retrieve-data-from-searchview-in-kotlin
        stockSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchStock(newText)
                } else {
                    getStockData()
                }
                return true
            }
        })

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val branchStock = intent.extras?.getParcelable<BuildingModel>("branchName")!!
        when (item.itemId) {
            R.id.item_add -> startActivityForResult(
                intentFor<StockActivity>().putExtra(
                    "branchName",
                    branchStock
                ), 0
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStockClick(stock: StockModel) {
        startActivityForResult(intentFor<StockActivity>().putExtra("stock_edit", stock), 0)

    }


    override fun onAddStockClick(stock: StockModel) {
        stock.inStock++
        app.stocks.update(stock)
    }

    override fun onMinusStockClick(stock: StockModel) {
        stock.inStock--
        app.stocks.update(stock)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        getStockData()
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun getStockData(){
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                stockList = mutableListOf()
                if (snapshot.exists()) {
                    for (stockSnap in snapshot.children) {
                        val stock = stockSnap.getValue(StockModel::class.java)
                        stockList.add(stock!!)
                    }
                }
                showStock(stockList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Failed", error.toException())
            }
        })
    }

    private fun loadBranchStock(){
        showStock(app.stocks.search(branchStock.id))
    }

    private fun showStock(stock: List<StockModel>) {
        recyclerView.adapter = StockAdapter(stock, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun searchStock(newText: String){
        foundList = mutableListOf()
        for(item in stockList){
            if(item.name.toLowerCase().contains(newText.toLowerCase())){
                foundList.add(item)
            }
        }
        showStock(foundList)
    }
}

