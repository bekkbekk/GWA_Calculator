package com.bekk.gwacalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var body: ConstraintLayout
    private lateinit var tvGWA: TextView
    private lateinit var rvSubj: RecyclerView
    private lateinit var etSubj: EditText
    private lateinit var etGrade: EditText
    private lateinit var etUnit: EditText
    private lateinit var btnAdd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        body = findViewById(R.id.body)
        tvGWA = findViewById(R.id.tvGWA)
        rvSubj = findViewById(R.id.rvSubj)
        etSubj = findViewById(R.id.etSubj)
        etGrade = findViewById(R.id.etGrade)
        etUnit = findViewById(R.id.etUnit)
        btnAdd = findViewById(R.id.btnAdd)

        val newList: MutableList<Info> = mutableListOf()
        val myAdapter = InfoAdapter(newList)
        rvSubj.adapter = myAdapter
        rvSubj.layoutManager = LinearLayoutManager(this)


        btnAdd.setOnClickListener {

            //if grades and units are empty
            if(etGrade.text.isEmpty()){
                etGrade.error = "Required"
                return@setOnClickListener
            }
            if(etUnit.text.isEmpty()){
                etUnit.error = "Required"
                return@setOnClickListener
            }

            val grade = "%.2f".format(etGrade.text.toString().toFloat())
            val unit = "%.1f".format(etUnit.text.toString().toFloat())
            val newInfo = Info(etSubj.text.toString(), grade, unit)
            newList.add(newInfo)
            myAdapter.notifyItemInserted(newList.size)
            etSubj.text = null
            etGrade.text = null
            etUnit.text = null
            tvGWA.text = updateGwa(myAdapter.infoList)
            rvSubj.smoothScrollToPosition(newList.size)
        }


        //updates GWA when recycler view is modified (removed a view)
    }


    private fun updateGwa(infoList: MutableList<Info>): String {

        var totalUnits = 0.0
        var totalGrade = 0.0
        var gradeUnit: Double

        for(i in infoList){
            totalUnits += i.unit.toDouble()
            gradeUnit = i.grade.toDouble() * i.unit.toDouble()
            totalGrade += gradeUnit
        }

        val gwa = totalGrade / totalUnits

        //round-off
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.HALF_UP

        return df.format(gwa)

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvDelete-> {
                tvGWA = findViewById(R.id.tvGWA)
                tvGWA.text = "0.00"
            }
        }
    }

}