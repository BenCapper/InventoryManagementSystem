package org.wit.inventory.activities

import android.content.Intent
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_building_list.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.wit.inventory.R
import org.wit.inventory.main.MainApp
import org.wit.inventory.models.BuildingModel


class BuildingListActivity : AppCompatActivity(), BuildingListener {

    lateinit var app: MainApp
    private val db = FirebaseDatabase.getInstance().reference.child("Building")
    private lateinit var builds: MutableList<BuildingModel>
    private lateinit var foundList: MutableList<BuildingModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBuildingData()
        setContentView(R.layout.activity_building_list)
        app = application as MainApp
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        //getBuildingData()
        toolbar.title = title
        setSupportActionBar(toolbar)



        //https://stackoverflow.com/questions/55949305/how-to-properly-retrieve-data-from-searchview-in-kotlin
        buildingSearch.setOnQueryTextListener(object :  SearchView.OnQueryTextListener  {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    search(newText)
                } else {
                    showBuildings(app.builds.findAll())
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
        when (item.itemId) {
            R.id.item_add -> startActivityForResult<BuildingActivity>(0)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBuildingClick(building: BuildingModel) {
        startActivityForResult(intentFor<StockListActivity>().putExtra("branchName", building), 0)
    }

    override fun onEditBuildingClick(building: BuildingModel) {
        startActivityForResult(intentFor<BuildingActivity>().putExtra("building_edit", building), 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        getBuildingData()
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadBuildings() {
        showBuildings(app.builds.findAll())

    }
    private fun getBuildingData(){
        db.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                builds = mutableListOf()
                if(snapshot.exists()){
                    for(buildSnap in snapshot.children){
                        val build = buildSnap.getValue(BuildingModel::class.java)
                        builds.add(build!!)
                    }
                }
                showBuildings(builds)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Failed", error.toException())
            }
        })
    }


    private fun showBuildings (buildings: List<BuildingModel>) {
        recyclerView.adapter = BuildingAdapter(buildings, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun search(newText: String){
        foundList = mutableListOf()
        for(item in builds){
            if(item.name.toLowerCase().contains(newText.toLowerCase())){
                foundList.add(item)
            }
        }
        showBuildings(foundList)
    }
}

