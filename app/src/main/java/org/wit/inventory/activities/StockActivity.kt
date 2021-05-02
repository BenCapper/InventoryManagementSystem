package org.wit.inventory.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_building.toolbarAdd
import kotlinx.android.synthetic.main.activity_stock.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.wit.inventory.R
import org.wit.inventory.helpers.readImage
import org.wit.inventory.helpers.readImageFromPath
import org.wit.inventory.helpers.showImagePicker
import org.wit.inventory.main.MainApp
import org.wit.inventory.models.BuildingModel
import org.wit.inventory.models.StockModel
import org.wit.inventory.models.generateRandomBuildId
import org.wit.inventory.models.generateRandomStockId

class StockActivity : AppCompatActivity(), AnkoLogger {

    var stock = StockModel()
    var branch = BuildingModel()
    lateinit var app: MainApp
    var edit = false
    val IMAGE_REQUEST = 1
    private val db = FirebaseDatabase.getInstance().reference.child("Stock")
    private lateinit var stocks: MutableList<StockModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)
        app = application as MainApp
        toolbarAdd.title = title
        setSupportActionBar(toolbarAdd)

        if (intent.hasExtra("stock_edit")) {
            edit = true
            stock = intent.extras?.getParcelable<StockModel>("stock_edit")!!
            stockImage.setImageBitmap(readImageFromPath(this, stock.image))
            stockName.setText(stock.name)
            stockWeight.setText(stock.weight)
            stockPrice.setText(stock.price.toString())
            stockDept.setText(stock.dept)
            stockQuantity.setText(stock.inStock.toString())
            btnAddStock.setText(R.string.save_stock)
            chooseStockImage.setText(R.string.change_stock_image)
        }

        btnAddStock.setOnClickListener() {
            if (intent.hasExtra("branchName")) {
                branch = intent.extras?.getParcelable<BuildingModel>("branchName")!!
                stock.branch = branch.id
                stock.inStock = 0
            }
            if (stockName.text.isNullOrEmpty()) {
                toast(R.string.nameToast)
            } else {
                stock.name = stockName.text.toString()
                if (stockDept.text.isNullOrEmpty()) {
                    toast(R.string.departmentToast)
                } else {
                    stock.dept = stockDept.text.toString()
                    stock.weight = stockWeight.text.toString()
                    if (stockWeight.text.isNullOrEmpty()) {
                        toast(R.string.weightToast)
                    } else {
                        if (stockPrice.text.toString().isEmpty()) {
                            toast(R.string.priceToast)
                        } else {
                            stock.price = stockPrice.text.toString().toDouble()
                            if (stockQuantity.text.toString().isEmpty()) {
                                toast(R.string.quantityToast)
                            } else {
                                stock.inStock = stockQuantity.text.toString().toLong()
                                if (edit) {
                                    app.stocks.update(stock.copy())
                                    info("update Button Pressed: $stockName")
                                    setResult(AppCompatActivity.RESULT_OK)
                                    finish()
                                } else {
                                    stock.id = generateRandomStockId()
                                    app.stocks.create(stock.copy())
                                    info("add Button Pressed: $stockName")
                                    setResult(AppCompatActivity.RESULT_OK)
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        }

        chooseStockImage.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST)
        }

        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_stock, menu)
        if (edit && menu != null) menu.getItem(0).isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_delete -> {
                app.stocks.delete(stock)
                finish()
            }
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_REQUEST -> {
                if (data != null) {
                    stock.image = data.data.toString()
                    stockImage.setImageBitmap(readImage(this, resultCode, data))
                    chooseStockImage.setText(R.string.change_stock_image)
                }
            }
        }
    }
}