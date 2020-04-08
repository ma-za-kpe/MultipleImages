package com.maku.multipleimages

import android.app.Application
import android.content.Context
import timber.log.Timber

class MultipleImages : Application() {

    //context
    init {
        instance = this
    }

    companion object {
        private var instance: MultipleImages? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        //timber
        Timber.plant(Timber.DebugTree())

        //fonts

    }
}