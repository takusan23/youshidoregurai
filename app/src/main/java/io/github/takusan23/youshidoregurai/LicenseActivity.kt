package io.github.takusan23.youshidoregurai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_license.*

class LicenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        licence_textview.text = """
google-ar/sceneform-android-sdk

Apache License 2.0
        """.trimIndent()

    }
}
