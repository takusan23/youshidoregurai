package io.github.takusan23.youshidoregurai.BottomFragment

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.takusan23.youshidoregurai.MainActivity
import io.github.takusan23.youshidoregurai.PaperListAdapter
import io.github.takusan23.youshidoregurai.R
import io.github.takusan23.youshidoregurai.SQLiteHelper.PaperSQLiteHelper
import kotlinx.android.synthetic.main.bottomfragment_paper_list.*

class PaperListBottomFragment : BottomSheetDialogFragment() {

    lateinit var paperListAdapter: PaperListAdapter
    val paperList = arrayListOf<ArrayList<String>>()

    lateinit var paperSQLiteHelper: PaperSQLiteHelper
    lateinit var sqLiteDatabase: SQLiteDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomfragment_paper_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDB()

        initRecyclerView()

        loadDB()

        //追加画面へ...
        bottom_fragment_list_add_button.setOnClickListener {
            val editPaperBottomFragment = EditPaperBottomFragment()
            fragmentManager?.let { it1 -> editPaperBottomFragment.show(it1, "edit") }
        }

    }

    fun loadDB() {
        paperList.clear()
        val query = sqLiteDatabase.query(
            PaperSQLiteHelper.TABLE_NAME,
            arrayOf("name", "height", "width"),
            null,
            null,
            null,
            null,
            null
        )
        query.moveToFirst()
        for (i in 0 until query.count) {
            val name = query.getString(0)
            val height = query.getInt(1).toString()
            val width = query.getInt(2).toString()
            val item = arrayListOf<String>().apply {
                add("")
                add(name)
                add(height)
                add(width)
            }
            paperList.add(item)
            query.moveToNext()
        }
        query.close()
        paperListAdapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        bottom_fragment_list_recyclerview.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(context)
        bottom_fragment_list_recyclerview.layoutManager =
            mLayoutManager as RecyclerView.LayoutManager?
        paperListAdapter = PaperListAdapter(paperList)
        paperListAdapter.sqLiteDatabase = sqLiteDatabase
        bottom_fragment_list_recyclerview.adapter = paperListAdapter

        val simpleItemTouchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(UP or DOWN, 0) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    val oldPos = viewHolder.adapterPosition
                    val item = paperList[oldPos]
                    paperList.removeAt(oldPos)
                    val newPos = target.adapterPosition
                    paperList.add(newPos, item)
                    paperListAdapter.notifyDataSetChanged()

                    sqLiteDatabase.delete(PaperSQLiteHelper.TABLE_NAME, null, null)

                    paperList.forEach {
                        val item = it
                        val name = item[1]
                        val height = item[2]
                        val width = item[3]
                        val contentValue = ContentValues().apply {
                            put("name", name)
                            put("height", height)
                            put("width", width)
                            put("setting", "")
                        }
                        sqLiteDatabase.insert(PaperSQLiteHelper.TABLE_NAME, null, contentValue)
                    }

                    loadDB()

                    if(activity is MainActivity){
                        (activity as MainActivity)?.loadDB()
                    }

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                }
            })

        simpleItemTouchHelper.attachToRecyclerView(bottom_fragment_list_recyclerview)

    }

    private fun initDB() {
        paperSQLiteHelper = PaperSQLiteHelper(context!!)
        sqLiteDatabase = paperSQLiteHelper.writableDatabase
        paperSQLiteHelper.setWriteAheadLoggingEnabled(false) //先読み？高速化無効。
    }

}