package com.app.pixman.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    object RequestCode {
        const val StoragePermission = 0x0000
    }

    fun requestStoragePermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                RequestCode.StoragePermission
            )
        }
    }

    fun hasStoragePermission(context: Context): Boolean {
        return hasPermission(context, mutableListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun hasPermission(context: Context, permissionList: MutableList<String>): Boolean {
        for (permission in permissionList) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_DENIED
            ) {
                return false
            }
        }
        return true
    }

    fun verifyResult(requestCode: Int, grantResults: IntArray): Boolean {
        if (requestCode == RequestCode.StoragePermission) {
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    return false
                }
            }
            return true
        } else {
            return false
        }
    }
}