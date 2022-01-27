package com.bekk.gwacalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

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

        updateGwa(getIemList())
        setUpRecyclerView()

        btnAdd.setOnClickListener {

            //if grades and units are empty
            if (etGrade.text.isEmpty()) {
                etGrade.error = "Required"
                return@setOnClickListener
            }
            if (etUnit.text.isEmpty()) {
                etUnit.error = "Required"
                return@setOnClickListener
            }

            val subj = etSubj.text.toString().trim()
            val grade = "%.2f".format(etGrade.text.toString().toFloat())
            val unit = "%.1f".format(etUnit.text.toString().toFloat())

            addRecord(etSubj, etGrade, etUnit, subj, grade, unit)

            /*
            val grade = "%.2f".format(etGrade.text.toString().toFloat())
            val unit = "%.1f".format(etUnit.text.toString().toFloat())
            val newInfo = Info(0, etSubj.text.toString(), grade, unit)
            newList.add(newInfo)
            myAdapter.notifyItemInserted(newList.size)
            etSubj.text = null
            etGrade.text = null
            etUnit.text = null

            updateGwa(myAdapter.infoList)

            rvSubj.smoothScrollToPosition(newList.size)
             */

        }

    }

    fun updateGwa(infoList: MutableList<Info>) {

        tvGWA = findViewById(R.id.tvGWA)
        var totalUnits = 0.0
        var totalGrade = 0.0
        var gradeUnit: Double

        for (i in infoList) {
            totalUnits += i.unit.toDouble()
            gradeUnit = i.grade.toDouble() * i.unit.toDouble()
            totalGrade += gradeUnit
        }

        val gwa = totalGrade / totalUnits

        //round-off
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.HALF_UP

        if (df.format(gwa).toFloat() > 0) {
            tvGWA.text = "%.2f".format(df.format(gwa).toFloat())
        } else {
            tvGWA.text = "0.00"
        }

    }

    private fun addRecord(
        etSubj: EditText,
        etGrade: EditText,
        etUnit: EditText,
        subj: String,
        grade: String,
        unit: String
    ) {
        val dbHandler = DataBaseHandler(this)
        val status = dbHandler.addData(Info(0, subj, grade, unit))

        if (status > -1) {
            etSubj.text = null
            etGrade.text = null
            etUnit.text = null

            updateGwa(getIemList())
            setUpRecyclerView()
        }
    }

    fun updateRecord(info: Info){
        val dbHandler = DataBaseHandler(this)
        val status = dbHandler.updateData(info)
        if (status > -1){
            setUpRecyclerView()
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT)
        }
    }

    fun deleteRecord(info: Info) {
        val dbHandler = DataBaseHandler(this)
        val status = dbHandler.deleteData(info)
        if (status > -1){
            setUpRecyclerView()
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT)
        }
    }

    fun setUpRecyclerView() {

        val dbHandler = DataBaseHandler(this)
        rvSubj = findViewById(R.id.rvSubj)

        if (getIemList().size > 0) { // kung may laman yung database

            val myAdapter = InfoAdapter(this, getIemList())
            rvSubj.adapter = myAdapter
            rvSubj.layoutManager = LinearLayoutManager(this)

            rvSubj.smoothScrollToPosition(getIemList().size - 1)
        }
    }

    private fun getIemList(): MutableList<Info> {
        val dbHandler = DataBaseHandler(this)
        return dbHandler.viewData()
    }

}