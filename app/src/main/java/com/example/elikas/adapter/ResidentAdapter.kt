package com.example.elikas.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.elikas.R
import com.example.elikas.data.Resident


class ResidentAdapter: ListAdapter<Resident, ResidentAdapter.ResidentViewHolder>(ResidentDiffCallback()){

    private lateinit var onResidentClick: OnResidentCheckListener

    interface OnResidentCheckListener {
        fun onResidentCheck(resident: String)
        fun onResidentUncheck(resident: String)
    }

    fun setListener(onResidentClick: OnResidentCheckListener) {
        this.onResidentClick = onResidentClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResidentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.resident_item, parent, false)

        return ResidentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResidentViewHolder, position: Int) {
        //holder.familyCode.text = residentList[position].name.toString()
        //getItem(position)
        val adapterResidents = currentList
        val resident = adapterResidents[position]

        val color = if (position % 2 == 0) R.color.inactive else R.color.white
        holder.rootView.setBackgroundResource(color)
        holder.bind(resident)
        holder.setColorType(resident.sectoral_classification)
    }

    inner class ResidentViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val checkBoxResident: CheckBox = view.findViewById(R.id.checkBoxResident)
        private val circleSectoral: View = view.findViewById(R.id.circle)

        val rootView = view
        fun bind(resident: Resident) {
            checkBoxResident.text = resident.name

            /*rootView.setOnClickListener{
                checkBoxResident.isChecked = !checkBoxResident.isChecked
                //resident.is_checked = !resident.is_checked
                //checkBoxResident.isChecked = resident.is_checked
                if (checkBoxResident.isChecked) {
                    onResidentClick.onResidentCheck(resident)
                } else {
                    onResidentClick.onResidentUncheck(resident)
                }
            }*/

            checkBoxResident.setOnCheckedChangeListener(null);
            //if true, your checkbox will be selected, else unselected
            checkBoxResident.isChecked = resident.is_checked
            checkBoxResident.setOnCheckedChangeListener { buttonView, isChecked ->
                resident.is_checked = isChecked
                for(res in currentList.indices) {
                    if(resident.id == currentList[res].id) {
                        currentList[res].is_checked = resident.is_checked
                        notifyItemChanged(res)
                        //if full Resident is needed, check is_checked and if is_family_head = 'Yes'
                        //then call the callback interfaces
                    }
                }
                if(resident.is_checked) {
                    onResidentClick.onResidentCheck(resident.family_code)
                } else {
                    onResidentClick.onResidentUncheck(resident.family_code)
                }
            }
        }
        fun setColorType(sectoral_classification: String) {
            val gradient = circleSectoral.background as GradientDrawable
            val context = rootView.context

            when(sectoral_classification) {
                "Children" -> {
                    gradient.setColor(ContextCompat.getColor(context, R.color.children))
                }
                "Lactating" -> {
                    gradient.setColor(ContextCompat.getColor(context, R.color.lactating))
                }
                "Person with Disability" -> {
                    gradient.setColor(ContextCompat.getColor(context, R.color.pwd))
                }
                "Pregnant" -> {
                    gradient.setColor(ContextCompat.getColor(context, R.color.pregnant))
                }
                "Senior Citizen" -> {
                    gradient.setColor(ContextCompat.getColor(context, R.color.senior))
                }
                "Solo Parent" -> {
                    gradient.setColor(ContextCompat.getColor(context, R.color.solo_parent))
                }
                "None" -> {
                    gradient.setColor(ContextCompat.getColor(context, android.R.color.transparent))
                }

            }
        }
    }

    override fun onViewRecycled(holder: ResidentViewHolder) {
        //holder.checkBoxResident.isChecked = false // - this line do the trick
        super.onViewRecycled(holder)
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
