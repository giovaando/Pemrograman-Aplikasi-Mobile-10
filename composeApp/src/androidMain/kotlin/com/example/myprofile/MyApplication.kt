package com.example.myprofile

import android.app.Application
import com.example.myprofile.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Android Application class.
 * Menginisialisasi Koin DI saat aplikasi pertama kali dibuat.
 * startKoin dipanggil sekali di sini agar tersedia di seluruh app.
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApplication)
            modules(appModules)
        }
    }
}
