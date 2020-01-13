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

class EditPaperBottomFragment(val name: String = "") : BottomSheetDialogFragment() {

    lateinit var paperSQLiteHelper: PaperSQLiteHelper
    lateinit var sqLiteDatabase: SQLiteDatabase

    //更新の場合
    var isUpdate = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomfragment_edit_paper, container, false)
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
                put("name", name)
                put("height", height)
                put("width", width)
                put("setting", "")
            }
            //追加 or アップデート
            if (isUpdate) {
                //同じ名前に上書き
                sqLiteDatabase.update(
                    PaperSQLiteHelper.TABLE_NAME,
                    contentValues,
                    "name=?",
                    arrayOf(name)
                )
            } else {
                sqLiteDatabase.insert(PaperSQLiteHelper.TABLE_NAME, null, contentValues)
            }
            if (activity is MainActivity) {
                //再読み込み
                (activity as MainActivity).loadDB()
            }
            dismiss()
        }

        if (name.isNotEmpty()) {
            val query = sqLiteDatabase.query(
                PaperSQLiteHelper.TABLE_NAME,
                arrayOf("name", "height", "width"),
                "name=?",
                arrayOf(name),
                null,
                null,
                null
            )
            query.moveToFirst()
            val name = query.getString(0)
            val height = query.getInt(1)
            val width = query.getInt(2)
            bottomfragment_edit_paper_name.setText(name)
            bottomfragment_edit_paper_height.setText(height.toString())
            bottomfragment_edit_paper_width.setText(width.toString())
            query.close()
            isUpdate = true
            bottomfragment_edit_paper_add_button.text = getString(R.string.change)
        }
    }

    private fun initDB() {
        paperSQLiteHelper = PaperSQLiteHelper(context!!)
        sqLiteDatabase = paperSQLiteHelper.writableDatabase
        paperSQLiteHelper.setWriteAheadLoggingEnabled(false) //先読み？高速化無効。
    }

}