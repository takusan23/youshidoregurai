package io.github.takusan23.youshidoregurai

import android.app.Activity
import android.app.ActivityManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.rendering.MaterialFactory
import android.view.WindowManager
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.TransformableNode
import io.github.takusan23.youshidoregurai.BottomFragment.EditPaperBottomFragment
import io.github.takusan23.youshidoregurai.BottomFragment.PaperListBottomFragment
import io.github.takusan23.youshidoregurai.SQLiteHelper.PaperSQLiteHelper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment

    //lateinit var modelRenderable: ModelRenderable

    //用紙
    lateinit var paperA4ModelRenderable: ModelRenderable
    lateinit var paperB5ModelRenderable: ModelRenderable
    lateinit var paperHagakiRenderable: ModelRenderable

    //用紙のVector。多分取得方法あると思う。
    lateinit var paperA4Vector3: Vector3
    lateinit var paperB5Vector3: Vector3
    lateinit var paperHagakiVector3: Vector3

    //データベース
    lateinit var paperSQLiteHelper: PaperSQLiteHelper
    lateinit var sqLiteDatabase: SQLiteDatabase

    //データベースの値から作った物体配列
    val renderableList = arrayListOf<ModelRenderable>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //条件満たしてなければActivity終了させる
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }
        setContentView(R.layout.activity_main)

        //DB初期化
        initDB()
        //よみこみ
        loadDB()

        //ArFragment取得
        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

        //ステータスバー透明化＋タイトルバー非表示＋ノッチ領域にも侵略
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val attrib = window.attributes
            attrib.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        //それぞれ初期化
        //  initA4()
        //  initB5()
        //  initHagaki()

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

            //モデル取得
            val tag = ar_tablayout.getTabAt(ar_tablayout.selectedTabPosition)?.tag as Int
            val model = renderableList[tag]

            //初期化済みのとき、利用可能
            // Create the Anchor.
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment.arSceneView.scene)
            // Create the transformable andy and add it to the anchor.
            val node = TransformableNode(arFragment.transformationSystem)
            node.setParent(anchorNode)
            node.scaleController.sensitivity = 0f   //感度を０にするとズームできなくなる？
            node.renderable = model
            node.select()

            node.setOnTapListener { hitTestResult, motionEvent ->
                //削除するか？ボトムシートで
                val modelBottomFragment = ModelBottomFragment(node)
                modelBottomFragment.show(supportFragmentManager, "model_bottom")
            }
        }

        ar_paper_add_button.setOnClickListener {
            val paperListBottomFragment = PaperListBottomFragment()
            paperListBottomFragment.show(supportFragmentManager, "list")
        }

        ar_setting_button.setOnClickListener {
            val intent = Intent(this, LicenseActivity::class.java)
            startActivity(intent)
        }

    }

    private fun initDB() {
        paperSQLiteHelper = PaperSQLiteHelper(this)
        sqLiteDatabase = paperSQLiteHelper.writableDatabase
        paperSQLiteHelper.setWriteAheadLoggingEnabled(false) //先読み？高速化無効。
    }

    fun loadDB() {
        ar_tablayout.removeAllTabs()
        renderableList.clear()
        if (::sqLiteDatabase.isInitialized) {
            val query = sqLiteDatabase.query(
                PaperSQLiteHelper.TABLE_NAME,
                arrayOf("name", "height", "width"),
                null,
                null,
                null,
                null,
                null
            )
            //0だったら動かないように
            if (query.count != 0) {
                query.moveToFirst()
                for (i in 0 until query.count) {
                    //取得
                    val name = query.getString(0)
                    val height = query.getInt(1)
                    val width = query.getInt(2)
                    //生成
                    val vector = Vector3(height / 1000f, 0.05f, width / 1000f)
                    MaterialFactory.makeOpaqueWithColor(
                        this,
                        com.google.ar.sceneform.rendering.Color(Color.BLUE)
                    ).thenAccept { material ->
                        val renderable = ShapeFactory.makeCube(vector, Vector3.zero(), material)
                        renderableList.add(renderable)
                        //TabItem作成
                        val item = ar_tablayout.newTab()
                        item.apply {
                            text = name
                            tag = (renderableList.size - 1)//配列の位置など
                        }
                        ar_tablayout.addTab(item)
                    }
                    query.moveToNext()
                }
            } else {
                //初回起動時。A4 B5 はがき を生成する。
                initDBHagaki()
                initDBA4()
                initDBB5()
                //再読み込み
                loadDB()
            }
            query.close()
        }
    }

    //はがき
    private fun initDBHagaki() {
        val height = 100
        val width = 148
        val name = "はがき"
        val contentValues = ContentValues()
        contentValues.apply {
            put("name", name)
            put("height", height)
            put("width", width)
            put("setting", "")
        }
        sqLiteDatabase.insert(PaperSQLiteHelper.TABLE_NAME, null, contentValues)
    }

    //B5用紙
    private fun initDBB5() {
        val height = 182
        val width = 257
        val name = "B5"
        val contentValues = ContentValues()
        contentValues.apply {
            put("name", name)
            put("height", height)
            put("width", width)
            put("setting", "")
        }
        sqLiteDatabase.insert(PaperSQLiteHelper.TABLE_NAME, null, contentValues)
    }

    //A4用紙
    private fun initDBA4() {
        val height = 297
        val width = 210
        val name = "A4"
        val contentValues = ContentValues()
        contentValues.apply {
            put("name", name)
            put("height", height)
            put("width", width)
            put("setting", "")
        }
        sqLiteDatabase.insert(PaperSQLiteHelper.TABLE_NAME, null, contentValues)
    }

    fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        val MIN_OPENGL_VERSION = 3.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Toast.makeText(activity, "SceneformにはAndroid N以降が必要です。", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }
        val openGlVersionString =
            (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).deviceConfigurationInfo.glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Toast.makeText(activity, "SceneformにはOpen GL 3.0以降が必要です。", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }
        return true
    }
}
