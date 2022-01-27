package com.bekk.gwacalculator

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHandler(val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "GWA_Calculator"

        private val TABLE_GWA = "GradesPerSubject"

        private val KEY_ID = "_id"
        private val KEY_SUBJECTS = "subject"
        private val KEY_GRADES = "grades"
        private val KEY_UNITS = "units"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //gagawa ng table sa database
        //CREATE TABLE_GWA(KEY_ID: INTEGER PRIMARY KEY, KEY_SUBJECTS: TEXT, KEY_GRADES: TEXT, KEY_UNITS: TEXT)
        val CREATE_GWA_TABLE =
            ("CREATE TABLE " + TABLE_GWA + "("
                    + KEY_ID + " INTEGER PRIMARY KEY,"
                    + KEY_SUBJECTS + " TEXT,"
                    + KEY_GRADES + " TEXT,"
                    + KEY_UNITS + " TEXT" + ")")

        //execute SQL
        db?.execSQL(CREATE_GWA_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_GWA")

        //calls onCreate on every upgrade
        onCreate(db)
    }

    @SuppressLint("Range")
    fun viewData(): MutableList<Info> {

        val listFromDB: MutableList<Info> = mutableListOf()

        // select data from table
        // give me everything that u have from this table
        val selectQuery = "SELECT * FROM $TABLE_GWA"

        val db = this.readableDatabase

        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLException) { //kung mag-error sa SQLite, cacatch yung error tapos ito nalang ang gagawin
            db.execSQL(selectQuery)
            return mutableListOf()
        }

        var id: Int
        var subj: String
        var grade: String
        var unit: String

        if (cursor.moveToFirst()){ // true if the cursor moves to first
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                subj = cursor.getString(cursor.getColumnIndex(KEY_SUBJECTS))
                grade = cursor.getString(cursor.getColumnIndex(KEY_GRADES))
                unit = cursor.getString(cursor.getColumnIndex(KEY_UNITS))

                listFromDB.add(Info(id, subj, grade, unit))

            } while (cursor.moveToNext()) //move to next and then returns true
            // pag wala nang kasunod edi false na tas stop na sa loop
        }

        return listFromDB
    }

    fun addData(info: Info): Long {

        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_SUBJECTS, info.subj)
        contentValues.put(KEY_GRADES, info.grade)
        contentValues.put(KEY_UNITS, info.unit)

        // table
        // null
        // contents to add
        val success = db.insert(TABLE_GWA, null, contentValues)
        db.close()

        return success
    }

    fun updateData(info: Info): Int {

        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_SUBJECTS, info.subj)
        contentValues.put(KEY_GRADES, info.grade)
        contentValues.put(KEY_UNITS, info.unit)

        // table
        // updated contents
        // id to update
        // null
        val success = db.update(TABLE_GWA, contentValues, "$KEY_ID=${info.id}", null)
        db.close()

        return success
    }

    fun deleteData(info: Info): Int {

        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, info.id)

        // table
        // id to delete
        // null
        val success = db.delete(TABLE_GWA, "$KEY_ID=${info.id}", null)
        db.close()

        return success
    }


}