package io.github.takusan23.youshidoregurai

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.rendering.MaterialFactory
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.SurfaceView
import android.view.View.*
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment

    //lateinit var modelRenderable: ModelRenderable

    lateinit var paperA4ModelRenderable: ModelRenderable
    lateinit var paperB5ModelRenderable: ModelRenderable
    lateinit var paperHagakiRenderable: ModelRenderable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //条件満たしてなければActivity終了させる
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }
        setContentView(R.layout.activity_main)

        //ArFragment取得
        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment

        //ステータスバー透明化＋タイトルバー非表示＋ノッチ領域にも侵略
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val attrib = window.attributes
            attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        //それぞれ初期化
        initA4()
        initB5()
        initHagaki()

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

                //返す。
                val model = when(ar_tablayout.selectedTabPosition){
                    0->paperA4ModelRenderable
                    1->paperB5ModelRenderable
                    2->paperHagakiRenderable
                    else->paperA4ModelRenderable
                }

                //初期化済みのとき、利用可能
                // Create the Anchor.
                val anchor = hitResult.createAnchor()
                val anchorNode = AnchorNode(anchor)
                anchorNode.setParent(arFragment.arSceneView.scene)
                // Create the transformable andy and add it to the anchor.
                val node = TransformableNode(arFragment.transformationSystem)
                node.setParent(anchorNode)
                node.renderable = model
                node.select()

                node.setOnTapListener { hitTestResult, motionEvent ->
                    //削除するか？ボトムシートで
                    val modelBottomFragment = ModelBottomFragment(node)
                    modelBottomFragment.show(supportFragmentManager,"model_bottom")
                }

        }

    }

    //はがき
    private fun initHagaki() {
        val height = 100f
        val width = 148f
        // A4　用紙
        MaterialFactory.makeOpaqueWithColor(
            this,
            com.google.ar.sceneform.rendering.Color(Color.GREEN)
        ).thenAccept { material ->
            paperHagakiRenderable =
                ShapeFactory.makeCube(
                    Vector3(height / 1000f, 0.05f, width / 1000f),
                    Vector3.zero(),
                    material
                )
        }
    }

    //B5用紙
    private fun initB5() {
        val height = 182f
        val width = 257f
        // A4　用紙
        MaterialFactory.makeOpaqueWithColor(
            this,
            com.google.ar.sceneform.rendering.Color(Color.BLUE)
        ).thenAccept { material ->
            paperB5ModelRenderable =
                ShapeFactory.makeCube(
                    Vector3(height / 1000f, 0.05f, width / 1000f),
                    Vector3.zero(),
                    material
                )
        }
    }

    //A4用紙
    private fun initA4() {
        val height = 297f
        val width = 210f
        // A4　用紙
        MaterialFactory.makeOpaqueWithColor(
            this,
            com.google.ar.sceneform.rendering.Color(Color.RED)
        ).thenAccept { material ->
            paperA4ModelRenderable =
                ShapeFactory.makeCube(
                    Vector3(height / 1000f, 0.05f, width / 1000f),
                    Vector3.zero(),
                    material
                )
        }
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
