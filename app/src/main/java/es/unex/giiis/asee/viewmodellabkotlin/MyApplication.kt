package es.unex.giiis.asee.viewmodellabkotlin

import android.app.Application

class MyApplication : Application() {
    var appContainer: AppContainer? = null
    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}