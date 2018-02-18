package com.treehacks.hopify.hopify

import android.app.Application

import io.branch.referral.Branch

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Branch.enableLogging()
        Branch.getAutoInstance(this)
    }
}
