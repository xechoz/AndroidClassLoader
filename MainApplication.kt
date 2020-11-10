package xyz.icodes.ichat

import android.app.Application
import android.content.Context

class MainApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MainClassLoader.interceptClassLoader(base)
    }
}