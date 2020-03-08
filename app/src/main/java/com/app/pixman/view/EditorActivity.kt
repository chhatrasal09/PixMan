package com.app.pixman.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.pixman.R
import com.app.pixman.utils.ArgumentKey
import com.app.pixman.utils.FlipType
import com.app.pixman.utils.PermissionUtils
import com.app.pixman.utils.Utility
import kotlinx.android.synthetic.main.activity_editor.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.ArrayBlockingQueue


class EditorActivity : AppCompatActivity() {

    private var mImageUri: Uri? = null
    private var mTransformedImage: Bitmap? = null
    private var mImageBitmap: Bitmap? = null

    private var mActionQueue: Queue<UserAction> = ArrayBlockingQueue(100)
    private var mUndoActionQueue: MutableList<Bitmap?> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        getIntentData()
        setupView()
        setOnClickListeners()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtils.verifyResult(requestCode, grantResults)) {
            saveBitmapImageToFile()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mTransformedImage?.isRecycled == false) {
                mTransformedImage?.recycle()
            }
            if (mImageBitmap?.isRecycled == false) {
                mImageBitmap?.recycle()
            }
            for (item in mUndoActionQueue) {
                if (item?.isRecycled == false) {
                    item?.recycle()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIntentData() {
        intent?.apply {
            mImageUri = (getParcelableExtra(ArgumentKey.ImageUri) as? Uri)?.also {
                mImageBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(it))
            }
        }
    }

    private fun setupView() {
        preview_image?.setImageURI(mImageUri)
    }

    private fun setOnClickListeners() {
        preview_changes?.setOnClickListener { previewImage() }
        flip_horizontal?.setOnClickListener {
            mActionQueue.add(UserAction.FlipHorizontally)
        }
        flip_vertical?.setOnClickListener {
            mActionQueue.add(UserAction.FlipVertically)
        }
        opacity?.setOnClickListener {
            mActionQueue.add(UserAction.Opacity)
        }
        add_text?.setOnClickListener {
            mActionQueue.add(UserAction.AddText)
        }
        save?.setOnClickListener { saveBitmapImageToFile() }

        undo?.setOnClickListener { undoLastMove() }
    }

    private fun previewImage() {
        if (mTransformedImage == null) mTransformedImage = mImageBitmap
        while (mActionQueue.isNotEmpty()) {
            if (mUndoActionQueue.size == 3) {
                mUndoActionQueue.removeAt(0)
            }
            mUndoActionQueue.add(mTransformedImage ?: mImageBitmap)
            undo?.isEnabled = true
            when (mActionQueue.poll()) {
                UserAction.FlipHorizontally -> mTransformedImage?.let {
                    mTransformedImage = Utility.flipImage(it, FlipType.Horizontally)
                }
                UserAction.FlipVertically -> mTransformedImage?.let {
                    mTransformedImage = Utility.flipImage(it, FlipType.Vertically)
                }
                UserAction.Opacity -> mTransformedImage?.let {
                    mTransformedImage = Utility.setImageOpacity(it, (0.5 * 255).toInt())
                }
                UserAction.AddText -> mTransformedImage?.let {
                    mTransformedImage = Utility.addTextToImage(
                        it,
                        LayoutInflater.from(this).inflate(R.layout.center_text, null, false)
                    )
                }
            }
        }
        preview_image?.setImageBitmap(mTransformedImage)
    }

    private fun saveBitmapImageToFile() {
        if (PermissionUtils.hasStoragePermission(this)) {
            if (mTransformedImage == null) {
                mTransformedImage = mImageBitmap
            }
            mTransformedImage?.let {
                mTransformedImage = Utility.addBitmap(it, this)
            }
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "image.jpg"
            )
            val outputStream = FileOutputStream(file)
            mTransformedImage?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            Toast.makeText(this, "File saved to gallery", Toast.LENGTH_LONG).show()
            finish()
        } else {
            PermissionUtils.requestStoragePermission(this)
        }
    }

    private fun undoLastMove() {
        if (mUndoActionQueue.isNotEmpty()) {
            mTransformedImage = mUndoActionQueue[mUndoActionQueue.size - 1]
            mUndoActionQueue.removeAt(mUndoActionQueue.size - 1)
        }
        undo?.isEnabled = mUndoActionQueue.isNotEmpty()
        preview_image?.setImageBitmap(mTransformedImage)
    }

    companion object {
        fun startMe(context: Context?, uri: Uri?) =
            context?.startActivity(Intent(context, EditorActivity::class.java).apply {
                putExtra(ArgumentKey.ImageUri, uri)
            })
    }
}

sealed class UserAction {
    object FlipVertically : UserAction()
    object FlipHorizontally : UserAction()
    object Opacity : UserAction()
    object AddText : UserAction()
}
