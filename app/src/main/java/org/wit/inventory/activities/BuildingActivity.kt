package org.wit.inventory.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_building.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.inventory.R
import org.wit.inventory.helpers.readImage
import org.wit.inventory.helpers.readImageFromPath
import org.wit.inventory.helpers.showImagePicker
import org.wit.inventory.main.MainApp
import org.wit.inventory.models.BuildingModel
import org.wit.inventory.models.Location
import org.wit.inventory.models.generateRandomBuildId

class BuildingActivity : AppCompatActivity(), AnkoLogger {

    var building = BuildingModel()
    lateinit var app: MainApp
    var edit = false
    val IMAGE_REQUEST = 1
    val LOCATION_REQUEST = 2
    private val db = FirebaseDatabase.getInstance().reference.child("Building")
    private lateinit var builds: MutableList<BuildingModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building)
        app = application as MainApp
        toolbarAdd.title = title
        setSupportActionBar(toolbarAdd)

        if (intent.hasExtra("building_edit")) {
            edit = true
            building = intent.extras?.getParcelable<BuildingModel>("building_edit")!!
            buildingImage.setImageBitmap(readImageFromPath(this, building.image))
            buildingName.setText(building.name)
            buildingAddress.setText(building.address)
            btnAdd.setText(R.string.save_building)
            chooseImage.setText(R.string.change_building_image)
        }

        btnAdd.setOnClickListener() {
            building.name = buildingName.text.toString()
            building.address = buildingAddress.text.toString()
            if (building.name.isEmpty()) {
                toast(R.string.enter_building_name)
            } else {
                if (edit) {
                    app.builds.update(building)
                    info("update Button Pressed: $buildingName")
                    setResult(RESULT_OK)
                    finish()
                } else {
                    building.id = generateRandomBuildId()
                    app.builds.create(building)
                    info("add Button Pressed: $buildingName")
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }

        chooseImage.setOnClickListener {
            showImagePicker(this, IMAGE_REQUEST)
        }

        buildingLocation.setOnClickListener {
            val location = Location(52.245696, -7.139102, 15f)
            if (building.zoom != 0f) {
                location.lat =  building.lat
                location.lng = building.lng
                location.zoom = building.zoom
            }
            startActivityForResult(intentFor<MapsActivity>().putExtra("location", location), LOCATION_REQUEST)
        }
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_building, menu)
        if (edit && menu != null) menu.getItem(0).isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_delete -> {
                app.builds.delete(building)
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
                    building.image = data.getData().toString()
                    buildingImage.setImageBitmap(readImage(this, resultCode, data))
                    chooseImage.setText(R.string.change_building_image)
                }
            }
            LOCATION_REQUEST -> {
                if (data != null) {
                    val location = data.extras?.getParcelable<Location>("location")!!
                    building.lat = location.lat
                    building.lng = location.lng
                    building.zoom = location.zoom
                }
            }
        }
    }
}