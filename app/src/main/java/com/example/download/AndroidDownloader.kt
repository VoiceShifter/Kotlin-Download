package com.example.download

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

class AndroidDownloader (private val context: Context) : Downloader{
    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    override fun DownloadFile(url: String): Long {
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("image/jpeg")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI) //- select wifi or etc
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("image.jpg")
            //.addRequestHeader() - if download from API
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image.jpg")
        return downloadManager.enqueue(request)

    }

}