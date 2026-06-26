package id.vanard.ayatqu.di

import id.vanard.ayatqu.data.OnboardingPreference
import id.vanard.ayatqu.viewmodel.AppViewModel
import id.vanard.ayatqu.worker.QuranDownloadWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val appModule = module {
    single { OnboardingPreference(androidContext()) }
    viewModelOf(::AppViewModel)
    workerOf(::QuranDownloadWorker)
}
