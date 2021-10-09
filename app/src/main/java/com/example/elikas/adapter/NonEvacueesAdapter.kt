package com.example.elikas.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.elikas.R
import com.example.elikas.data.Resident
import com.example.elikas.data.User
import com.example.elikas.utils.SharedPreferenceUtil

class NonEvacueesAdapter: ListAdapter<Resident, RecyclerView.ViewHolder>(ResidentDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.non_evacuees_item, parent, false)

        return ResidentViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //holder.familyCode.text = residentList[position].name.toString()
        val resident = getItem(position)

        val color = if (position % 2 == 0) R.color.white else R.color.inactive
        (holder as ResidentViewHolder).rootView.setBackgroundResource(color)
        holder.bind(resident.name)
    }

    class ResidentViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtViewNonEvacuees: TextView = view.findViewById(R.id.txtViewResident)

        val rootView = view
        fun bind(name: String?) {
            txtViewNonEvacuees.text = name
        }
    }

    private class ResidentDiffCallback : DiffUtil.ItemCallback<Resident>() {

        override fun areItemsTheSame(oldItem: Resident, newItem: Resident): Boolean {
            return oldItem.family_code == newItem.family_code
        }

        override fun areContentsTheSame(oldItem: Resident, newItem: Resident): Boolean {
            return oldItem == newItem
        }
    }

}
