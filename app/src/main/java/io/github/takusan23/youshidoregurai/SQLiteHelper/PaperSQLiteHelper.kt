package io.github.takusan23.youshidoregurai.SQLiteHelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PaperSQLiteHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        // テーブル作成
        // SQLiteファイルがなければSQLiteファイルが作成される
        db?.execSQL(
            SQL_CREATE_ENTRIES
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // アップデートの判別
        db?.execSQL(
            SQL_DELETE_ENTRIES
        )
        onCreate(db)
    }

    companion object {
        // データーベースのバージョン
        private val DATABASE_VERSION = 1

        // データーベース名
        private val DATABASE_NAME = "Paper.db"
         val TABLE_NAME = "paper_db"
        private val HEIGHT = "height"
        private val WIDTH = "width"
        private val SETTING = "setting"
        private val NAME = "name"
        private val USER = "user_id"
        private val _ID = "_id"


        // , を付け忘れるとエラー
        private val SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                NAME + " TEXT ," +
                HEIGHT + " INTEGER ," +
                WIDTH + " INTEGER ," +
                SETTING + " TEXT" +
                ")"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

}