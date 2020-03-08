package com.app.pixman.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.pixman.R
import com.app.pixman.utils.RequestCode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        choose_image_button?.setOnClickListener {
            startActivityForResult(
                Intent.createChooser(Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                },"Select Image"),
                RequestCode.ImagePicker
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.ImagePicker -> {
                EditorActivity.startMe(this, data?.data)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
