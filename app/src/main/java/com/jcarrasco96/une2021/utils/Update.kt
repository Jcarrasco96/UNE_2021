package com.jcarrasco96.une2021.utils

import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jcarrasco96.apklisupdate.ApklisUpdate
import com.jcarrasco96.apklisupdate.UpdateCallback
import com.jcarrasco96.apklisupdate.models.AppUpdateInfo
import com.jcarrasco96.une2021.R

object Update {

    fun searchUpdate(context: Context, packageName: String, showErrors: Boolean = false) {
        val builderInfo = MaterialAlertDialogBuilder(context)
        builderInfo.setTitle("Buscando")
        builderInfo.setMessage("Buscando nueva actualización...")
        builderInfo.setCancelable(false)
        val alertDialogInfo = builderInfo.create()
        if (showErrors) {
            alertDialogInfo.show()
        }

        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle("Actualización")
        builder.setNegativeButton(R.string.cancel) { _: DialogInterface, _: Int -> }

        ApklisUpdate.hasAppUpdate(context, callback = object : UpdateCallback {
            override fun onNewUpdate(appUpdateInfo: AppUpdateInfo) {
                builder.setMessage("Existe una nueva actualización en Apklis. ¿Desea descargarla?")
                builder.setPositiveButton("Descargar") { _: DialogInterface, _: Int ->
                    val filename =
                        "${packageName}-v${appUpdateInfo.last_release.version_code}.apk"
                    val url =
                        "https://archive.apklis.cu/application/apk/${filename}?key=${appUpdateInfo.last_release.direct_key}"

                    executeDownload(context, url, filename)
                }
                alertDialogInfo.dismiss()
                builder.show()
            }

            override fun onOldUpdate(appUpdateInfo: AppUpdateInfo) {
                if (showErrors) {
                    builder.setMessage("Ya usted tiene la última actualización")
                    alertDialogInfo.dismiss()
                    builder.show()
                }
            }

            override fun onError(e: Throwable) {
                if (showErrors) {
                    builder.setMessage("Ocurrió un error al intentar buscar actualizaciones")
                    alertDialogInfo.dismiss()
                    builder.show()
                }
            }
        })
    }

    fun executeDownload(context: Context, url: String, filename: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI + DownloadManager.Request.NETWORK_MOBILE)
        request.setDescription("Descargar nueva actualización")
        request.setTitle("Descargando ${filename}...")
//        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
        val manager =
            context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }

}