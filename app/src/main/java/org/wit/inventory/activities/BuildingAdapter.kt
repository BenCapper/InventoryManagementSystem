package org.wit.inventory.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_building.view.*
import org.wit.inventory.R
import org.wit.inventory.helpers.readImageFromPath
import org.wit.inventory.models.BuildingModel

interface BuildingListener {
    fun onBuildingClick(building: BuildingModel)
    fun onEditBuildingClick(building: BuildingModel)
}

class BuildingAdapter constructor(private var buildings: List<BuildingModel>,
                                  private val listener: BuildingListener) :
    RecyclerView.Adapter<BuildingAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_building,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val building = buildings[holder.adapterPosition]
        holder.bind(building, listener)
    }

    override fun getItemCount(): Int = buildings.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(building: BuildingModel, listener : BuildingListener) {
            itemView.buildingName.text = building.name.capitalize()
            itemView.address.text = building.address.capitalize()
            itemView.imageIcon.setImageBitmap(readImageFromPath(itemView.context, building.image))
            itemView.setOnClickListener {listener.onBuildingClick(building)}
            itemView.edit.setOnClickListener {listener.onEditBuildingClick(building)}
        }
    }


}
