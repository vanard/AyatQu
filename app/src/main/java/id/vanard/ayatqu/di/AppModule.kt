package id.vanard.ayatqu.di

import com.google.firebase.auth.FirebaseAuth
import id.vanard.ayatqu.data.AdhanPreference
import id.vanard.ayatqu.data.AdhanScheduleCache
import id.vanard.ayatqu.data.LastReadPreference
import id.vanard.ayatqu.data.LanguagePreference
import id.vanard.ayatqu.data.OnboardingPreference
import id.vanard.ayatqu.data.PrayerTimeCache
import id.vanard.ayatqu.data.local.AyahAudioCache
import id.vanard.ayatqu.data.local.JuzLocalCache
import id.vanard.ayatqu.data.local.SurahDetailLocalCache
import id.vanard.ayatqu.data.local.SurahLocalCache
import id.vanard.ayatqu.data.remote.PrayerTimeApiService
import id.vanard.ayatqu.data.remote.PrayerTimeRetrofitClient
import id.vanard.ayatqu.data.remote.QiblaApiService
import id.vanard.ayatqu.data.remote.QuranApiService
import id.vanard.ayatqu.data.remote.QuranRetrofitClient
import id.vanard.ayatqu.data.repository.FirebaseAuthRepository
import id.vanard.ayatqu.data.repository.PrayerTimeRepositoryImpl
import id.vanard.ayatqu.data.repository.QiblaRepositoryImpl
import id.vanard.ayatqu.data.repository.QuranRepositoryImpl
import id.vanard.ayatqu.domain.repository.AuthRepository
import id.vanard.ayatqu.domain.repository.PrayerTimeRepository
import id.vanard.ayatqu.domain.repository.QiblaRepository
import id.vanard.ayatqu.domain.repository.QuranRepository
import id.vanard.ayatqu.domain.usecase.AuthUseCase
import id.vanard.ayatqu.core.navigation.NavigationManager
import id.vanard.ayatqu.core.navigation.NavigationManagerImpl
import id.vanard.ayatqu.presentation.auth.landing.LandingViewModel
import id.vanard.ayatqu.presentation.auth.login.LoginViewModel
import id.vanard.ayatqu.presentation.auth.signup.SignUpViewModel
import id.vanard.ayatqu.presentation.home.HomeViewModel
import id.vanard.ayatqu.presentation.onboarding.OnboardingViewModel
import id.vanard.ayatqu.presentation.profile.ProfileViewModel
import id.vanard.ayatqu.presentation.qibla.QiblaViewModel
import id.vanard.ayatqu.presentation.quran.detail.DetailSurahViewModel
import id.vanard.ayatqu.presentation.quran.list.QuranViewModel
import id.vanard.ayatqu.presentation.quran.juz.JuzDetailViewModel
import id.vanard.ayatqu.presentation.root.AppViewModel
import id.vanard.ayatqu.util.LocationHelper
import id.vanard.ayatqu.util.NetworkUtils
import id.vanard.ayatqu.worker.AdhanSchedulerWorker
import id.vanard.ayatqu.worker.QuranDownloadWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {
    // Preferences & Cache
    single { OnboardingPreference(androidContext()) }
    single { LastReadPreference(androidContext()) }
    single { SurahLocalCache(androidContext()) }
    single { SurahDetailLocalCache(androidContext()) }
    single { AyahAudioCache(androidContext()) }
    single { JuzLocalCache(androidContext()) }
    single { PrayerTimeCache(androidContext()) }
    single { AdhanPreference(androidContext()) }
    single { AdhanScheduleCache(androidContext()) }
    single { LanguagePreference(androidContext()) }

    // Firebase
    single { FirebaseAuth.getInstance() }

    // Utils
    single { NetworkUtils(androidContext()) }
    single { LocationHelper(androidContext()) }
    single { QuranRetrofitClient.createRetrofit(androidContext()) }
    single { get<Retrofit>().create(QuranApiService::class.java) } bind QuranApiService::class
    single { get<Retrofit>().create(QiblaApiService::class.java) } bind QiblaApiService::class
    single { PrayerTimeRetrofitClient.create(androidContext()) } bind PrayerTimeApiService::class

    // Repositories
    singleOf(::FirebaseAuthRepository) bind AuthRepository::class
    singleOf(::QuranRepositoryImpl) bind QuranRepository::class
    singleOf(::PrayerTimeRepositoryImpl) bind PrayerTimeRepository::class
    singleOf(::QiblaRepositoryImpl) bind QiblaRepository::class

    // Use cases
    singleOf(::AuthUseCase)
    singleOf(::NavigationManagerImpl) bind NavigationManager::class

    // ViewModels
    viewModelOf(::AppViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::LandingViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::QiblaViewModel)
    viewModelOf(::QuranViewModel)
    viewModelOf(::JuzDetailViewModel)
    viewModelOf(::DetailSurahViewModel)
    viewModelOf(::ProfileViewModel)

    // Workers
    workerOf(::QuranDownloadWorker)
    workerOf(::AdhanSchedulerWorker)
}
