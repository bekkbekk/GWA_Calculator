package com.bekk.gwacalculator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InfoAdapter (
    private val context: Context,
    val infoList: MutableList<Info>
        ) : RecyclerView.Adapter<InfoAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val subject: TextView = itemView.findViewById(R.id.etSubj)
        val grade: TextView = itemView.findViewById(R.id.etGrade)
        val unit: TextView = itemView.findViewById(R.id.etUnit)
        val delete: ImageButton = itemView.findViewById(R.id.ibDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            if(infoList[position].subj.trim() == ""){
                subject.text = "Subject ${position + 1}"
            } else {
                subject.text = infoList[position].subj.trim()
            }
            grade.text = "Grade: " + infoList[position].grade
            unit.text = "Units: " + infoList[position].unit
        }

        holder.delete.setOnClickListener {
            infoList.removeAt(position)
            if (context is MainActivity){
                context.updateGwa(infoList)
            }
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    fun updateGwa(): String {

        var totalUnits = 0.0
        var totalGrade = 0.0
        var gradeUnit: Double

        for(i in infoList){
            totalUnits += i.unit.toDouble()
            gradeUnit = i.grade.toDouble() * i.unit.toDouble()
            totalGrade += gradeUnit
        }

        val gwa = totalGrade / totalUnits

        return "%.2f".format(gwa)

    }

}