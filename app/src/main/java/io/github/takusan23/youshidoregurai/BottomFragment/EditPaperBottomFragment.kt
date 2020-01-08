package io.github.takusan23.youshidoregurai.BottomFragment

import android.content.ContentResolver
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.takusan23.youshidoregurai.MainActivity
import io.github.takusan23.youshidoregurai.R
import io.github.takusan23.youshidoregurai.SQLiteHelper.PaperSQLiteHelper
import kotlinx.android.synthetic.main.bottomfragment_edit_paper.*

class EditPaperBottomFragment :BottomSheetDialogFragment(){

    lateinit var paperSQLiteHelper: PaperSQLiteHelper
    lateinit var sqLiteDatabase: SQLiteDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomfragment_edit_paper,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //データベースしょきか
        initDB()

        //追加
        bottomfragment_edit_paper_add_button.setOnClickListener {
            //テキストボックスから取得
            val name = bottomfragment_edit_paper_name.text.toString()
            val height = bottomfragment_edit_paper_height.text.toString().toInt()
            val width = bottomfragment_edit_paper_width.text.toString().toInt()
            //追加
            val contentValues = ContentValues()
            contentValues.apply {
                put("name",name)
                put("height",height)
                put("width",width)
                put("setting","")
            }
            sqLiteDatabase.insert(PaperSQLiteHelper.TABLE_NAME,null,contentValues)
            if(activity is MainActivity){
                //再読み込み
                (activity as MainActivity).loadDB()
            }
        }


    }

    private fun initDB() {
        paperSQLiteHelper = PaperSQLiteHelper(context!!)
        sqLiteDatabase = paperSQLiteHelper.writableDatabase
        paperSQLiteHelper.setWriteAheadLoggingEnabled(false) //先読み？高速化無効。
    }

}