package com.example.challengme

import android.app.Application
import com.example.challengme.data.local.AuthManager

class ChallengMeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthManager.init(this)
    }
}