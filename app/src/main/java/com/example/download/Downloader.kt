package com.example.download

interface Downloader {
    fun DownloadFile(url: String) : Long

}