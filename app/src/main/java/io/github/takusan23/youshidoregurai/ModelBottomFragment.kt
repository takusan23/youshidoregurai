package io.github.takusan23.youshidoregurai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.bottomfragment_model.*
import com.google.ar.sceneform.math.Quaternion


class ModelBottomFragment(
    val anchor: AnchorNode,
    val node: TransformableNode,
    val model: ModelRenderable,
    val arFragment: ArFragment,
    val vector3: Vector3
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomfragment_model, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as MainActivity

        //削除
        bottom_fragment_model_delete_button.setOnClickListener {
            node.isEnabled = false
        }

    }
}