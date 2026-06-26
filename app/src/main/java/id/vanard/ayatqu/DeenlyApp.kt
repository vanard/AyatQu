package id.vanard.ayatqu

import android.app.Application
import id.vanard.ayatqu.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

class AyatQuApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AyatQuApp)
            workManagerFactory()
            modules(appModule)
        }
    }
}
