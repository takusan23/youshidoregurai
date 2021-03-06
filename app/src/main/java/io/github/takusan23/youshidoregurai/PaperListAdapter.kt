package io.github.takusan23.youshidoregurai

import android.database.sqlite.SQLiteDatabase
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.takusan23.youshidoregurai.BottomFragment.EditPaperBottomFragment
import io.github.takusan23.youshidoregurai.BottomFragment.PaperListBottomFragment
import io.github.takusan23.youshidoregurai.SQLiteHelper.PaperSQLiteHelper

class PaperListAdapter(private val arrayListArrayAdapter: ArrayList<ArrayList<String>>) :
    RecyclerView.Adapter<PaperListAdapter.ViewHolder>() {

    lateinit var mainActivity: MainActivity
    lateinit var paperListBottomFragment: PaperListBottomFragment
    lateinit var sqLiteDatabase: SQLiteDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_paper_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayListArrayAdapter.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val context = holder.titleTextView.context

        val item = arrayListArrayAdapter[position] as ArrayList<String>
        val name = item.get(1)
        val height = item.get(2)
        val width = item.get(3)

        holder.titleTextView.text = name
        holder.sizeTextView.text = "$height x $width"

        holder.deleteButton.setOnClickListener {
            if (::sqLiteDatabase.isInitialized) {
                sqLiteDatabase.delete(PaperSQLiteHelper.TABLE_NAME, "name=?", arrayOf(name))
                //DB更新
                mainActivity.loadDB()
                paperListBottomFragment.loadDB()
            }
        }

        holder.editButton.setOnClickListener {
            //編集画面
            if (::sqLiteDatabase.isInitialized) {
                mainActivity.supportFragmentManager.let { it1 ->
                    val editPaperBottomFragment = EditPaperBottomFragment(name)
                    editPaperBottomFragment.paperListBottomFragment = paperListBottomFragment
                    editPaperBottomFragment.show(it1, "edit")
                }
            }
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var titleTextView: TextView
        var sizeTextView: TextView
        var deleteButton: ImageView
        var editButton: ImageView

        init {
            titleTextView = itemView.findViewById(R.id.adapter_title)
            sizeTextView = itemView.findViewById(R.id.adapter_size)
            editButton = itemView.findViewById(R.id.adapter_edit)
            deleteButton = itemView.findViewById(R.id.adapter_delete)
        }
    }
}
