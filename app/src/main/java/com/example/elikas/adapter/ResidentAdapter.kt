package com.example.elikas.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.elikas.R
import com.example.elikas.data.Residents

class ResidentAdapter (val residentList: List<Residents> ): RecyclerView.Adapter<ResidentAdapter.ViewHolder>(){

    class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val familyCode: CheckBox = view.findViewById(R.id.familyCode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.resident_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.familyCode.text = residentList[position].name.toString()
    }

    override fun getItemCount(): Int {
        return residentList.size
    }

}
