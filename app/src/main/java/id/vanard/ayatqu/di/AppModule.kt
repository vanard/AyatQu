package id.vanard.ayatqu.di

import com.google.firebase.auth.FirebaseAuth
import id.vanard.ayatqu.data.OnboardingPreference
import id.vanard.ayatqu.data.repository.FirebaseAuthRepository
import id.vanard.ayatqu.domain.repository.AuthRepository
import id.vanard.ayatqu.domain.usecase.AuthUseCase
import id.vanard.ayatqu.viewmodel.AppViewModel
import id.vanard.ayatqu.viewmodel.AuthViewModel
import id.vanard.ayatqu.worker.QuranDownloadWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // Preferences
    single { OnboardingPreference(androidContext()) }

    // Firebase
    single { FirebaseAuth.getInstance() }

    // Repository
    singleOf(::FirebaseAuthRepository) bind AuthRepository::class

    // Use cases
    singleOf(::AuthUseCase)

    // ViewModels
    viewModelOf(::AppViewModel)
    viewModelOf(::AuthViewModel)

    // Workers
    workerOf(::QuranDownloadWorker)
}
