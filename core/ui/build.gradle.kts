plugins {
    id("ayatqu.android.library")
    id("ayatqu.android.compose")
}

android {
    namespace = "id.vanard.ayatqu.core.ui"
}

dependencies {
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
