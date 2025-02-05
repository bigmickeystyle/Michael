plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    kotlin("kapt") version "2.1.0"
    alias(libs.plugins.hilt.android) apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
}