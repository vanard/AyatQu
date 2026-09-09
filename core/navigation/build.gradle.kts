plugins {
    id("ayatqu.android.library")
    id("ayatqu.android.compose")
}

android {
    namespace = "id.vanard.ayatqu.core.navigation"
}

dependencies {
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.savedstate.compose)
    implementation(libs.kotlinx.serialization.core)
}
