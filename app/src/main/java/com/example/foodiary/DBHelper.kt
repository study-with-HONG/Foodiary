package com.example.foodiary

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class DBHelper(context: Context?, name:String?): SQLiteOpenHelper(context, name, null, 1){
    companion object{
        private var dbhelper:DBHelper? = null
        fun getInstance(context:Context, filename: String) : DBHelper {
            if(dbhelper == null){
                dbhelper = DBHelper(context, filename)
            }
            return dbhelper!!
        }
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val sql:String = "CREATE TABLE if not exists foodiarydb(" +
                         "seq integer primary key AUTOINCREMENT, " +
                         "path string,      date string, " +
                         "category integer,  menu string)"
        db?.execSQL(sql)
    }
    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        var sql:String = "DROP TABLE IF EXISTS foodiarydb"
        db?.execSQL(sql)
        onCreate(db)
    }
    fun insert(data:dataDto){
        val sql = "INSERT INTO foodiarydb(path, date, category, menu) " +
                  "VALUES('${data.path}', '${data.date}', ${data.category}, '${data.menu}')"
        val db = this.writableDatabase
        db.execSQL(sql)
    }
    @SuppressLint("Range")
    fun select(selectdate:String):ArrayList<dataDto>{
        var list = ArrayList<dataDto>()
        var sql = "SELECT * FROM foodiarydb " +
                  "WHERE date = '${selectdate}' " +
                  "ORDER BY category"
        var db = this.readableDatabase

        var cursor = db.rawQuery(sql,null)

        while(cursor.moveToNext()){
            val seq = cursor.getInt(cursor.getColumnIndex("seq"))
            val path = cursor.getString(cursor.getColumnIndex("path"))
            val date = cursor.getString(cursor.getColumnIndex("date"))
            val cate = cursor.getInt(cursor.getColumnIndex("category"))
            val menu = cursor.getString(cursor.getColumnIndex("menu"))

            print("불러오기!!!!! $path $date $cate $menu")
            list.add(dataDto(seq, path, date, cate, menu))
        }
        cursor.close()
        return list
    }
    fun delete(data:dataDto){
        var sql = "DELETE FROM foodiarydb " +
                  "WHERE path = '${data.path}' AND date = '${data.date}' AND menu = '${data.menu}'"
        var db = this.writableDatabase
        db.execSQL(sql)

        print("db삭제확인!!!!!!!!!! $db")
    }
    fun update(data:dataDto){
        var sql = "UPDATE foodiarydb " +
                  "SET path = '${data.path}', date = '${data.date}', category = ${data.category}, menu = '${data.menu}"
        var db = this.writableDatabase
        db.execSQL(sql)

        print("db수정확인!!!!!!!!!! $db")
    }
}