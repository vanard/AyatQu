package id.vanard.ayatqu.di

import com.google.firebase.auth.FirebaseAuth
import id.vanard.ayatqu.data.AdhanPreference
import id.vanard.ayatqu.data.LastReadPreference
import id.vanard.ayatqu.data.OnboardingPreference
import id.vanard.ayatqu.data.PrayerTimeCache
import id.vanard.ayatqu.data.local.AyahAudioCache
import id.vanard.ayatqu.data.local.SurahDetailLocalCache
import id.vanard.ayatqu.data.local.SurahLocalCache
import id.vanard.ayatqu.data.remote.PrayerTimeApiService
import id.vanard.ayatqu.data.remote.PrayerTimeRetrofitClient
import id.vanard.ayatqu.data.remote.QuranApiService
import id.vanard.ayatqu.data.remote.QuranRetrofitClient
import id.vanard.ayatqu.data.repository.FirebaseAuthRepository
import id.vanard.ayatqu.data.repository.PrayerTimeRepositoryImpl
import id.vanard.ayatqu.data.repository.QuranRepositoryImpl
import id.vanard.ayatqu.domain.repository.AuthRepository
import id.vanard.ayatqu.domain.repository.PrayerTimeRepository
import id.vanard.ayatqu.domain.repository.QuranRepository
import id.vanard.ayatqu.domain.usecase.AuthUseCase
import id.vanard.ayatqu.util.LocationHelper
import id.vanard.ayatqu.util.NetworkUtils
import id.vanard.ayatqu.viewmodel.AppViewModel
import id.vanard.ayatqu.viewmodel.AuthViewModel
import id.vanard.ayatqu.viewmodel.DetailSurahViewModel
import id.vanard.ayatqu.viewmodel.HomeViewModel
import id.vanard.ayatqu.viewmodel.QuranViewModel
import id.vanard.ayatqu.worker.AdhanSchedulerWorker
import id.vanard.ayatqu.worker.QuranDownloadWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // Preferences & Cache
    single { OnboardingPreference(androidContext()) }
    single { LastReadPreference(androidContext()) }
    single { SurahLocalCache(androidContext()) }
    single { SurahDetailLocalCache(androidContext()) }
    single { AyahAudioCache(androidContext()) }
    single { PrayerTimeCache(androidContext()) }
    single { AdhanPreference(androidContext()) }

    // Firebase
    single { FirebaseAuth.getInstance() }

    // Utils
    single { NetworkUtils(androidContext()) }
    single { LocationHelper(androidContext()) }
    single { QuranRetrofitClient.create(androidContext()) } bind QuranApiService::class
    single { PrayerTimeRetrofitClient.create(androidContext()) } bind PrayerTimeApiService::class

    // Repositories
    singleOf(::FirebaseAuthRepository) bind AuthRepository::class
    singleOf(::QuranRepositoryImpl) bind QuranRepository::class
    singleOf(::PrayerTimeRepositoryImpl) bind PrayerTimeRepository::class

    // Use cases
    singleOf(::AuthUseCase)

    // ViewModels
    viewModelOf(::AppViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::QuranViewModel)
    viewModelOf(::DetailSurahViewModel)

    // Workers
    workerOf(::QuranDownloadWorker)
    workerOf(::AdhanSchedulerWorker)
}
