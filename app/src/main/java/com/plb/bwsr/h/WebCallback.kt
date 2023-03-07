package com.plb.bwsr.h

interface WebCallback {

    fun onWebStarted(url: String)

    fun onWebFinish(url: String)

    fun onWebProgress(progress: Int)

}