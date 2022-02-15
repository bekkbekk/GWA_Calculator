package com.bekk.gwacalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    //ads
    private lateinit var mAdView: AdView

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

        //Mobile Ads //dependencies
        MobileAds.initialize(this@MainActivity)
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        btnAdd.setOnClickListener {

            //if grades and units are empty
            if (etGrade.text.isEmpty()) {
                etGrade.error = "Required"
                etGrade.requestFocus()
                return@setOnClickListener
            }
            if (etUnit.text.isEmpty()) {
                etUnit.error = "Required"
                etUnit.requestFocus()
                return@setOnClickListener
            }

            val subj = etSubj.text.toString().trim()
            val grade = "%.2f".format(etGrade.text.toString().toFloat())
            val unit = "%.1f".format(etUnit.text.toString().toFloat())

            addRecord(etSubj, etGrade, etUnit, subj, grade, unit)

            etSubj.requestFocus()

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteAll -> {
                if (getIemList().size > 0) {
                    showAlertDialogBox()
                } else {
                    Toast.makeText(this, "No items to delete", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return super.onOptionsItemSelected(item)
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

    fun updateRecord(info: Info) {
        val dbHandler = DataBaseHandler(this)
        val status = dbHandler.updateData(info)
        if (status > -1) {
            setUpRecyclerView()
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT)
        }
    }

    fun deleteRecord(info: Info) {
        val dbHandler = DataBaseHandler(this)
        val status = dbHandler.deleteData(info)
        if (status > -1) {
            setUpRecyclerView()
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT)
        }
    }

    fun setUpRecyclerView() {

        rvSubj = findViewById(R.id.rvSubj)

        //if (getIemList().size > 0) { // kung may laman yung database

        val myAdapter = InfoAdapter(this, getIemList())
        rvSubj.adapter = myAdapter
        rvSubj.layoutManager = LinearLayoutManager(this)

        rvSubj.smoothScrollToPosition(getIemList().size)
        //}
    }

    private fun getIemList(): MutableList<Info> {
        val dbHandler = DataBaseHandler(this)
        return dbHandler.viewData()
    }

    private fun showAlertDialogBox() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Are you sure?")
            .setCancelable(false)
            .setMessage("Are you sure you want to delete all items?")
            .setPositiveButton("Yes") { dialog, which ->

                clearData()
                updateGwa(getIemList())
                val myAdapter = InfoAdapter(this, getIemList())
                rvSubj.adapter = myAdapter
                rvSubj.layoutManager = LinearLayoutManager(this)

            }
            .setNegativeButton("No") { dialog, which ->

            }
            .show()


    }

    private fun clearData() {
        for (info in getIemList()) {
            deleteRecord(info)
        }
    }

}