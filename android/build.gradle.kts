plugins {
    id("org.jetbrains.compose") version "1.1.0"
    id("com.android.application")
    kotlin("android")
}

group = "com.numq"
version = "1.0"

repositories {
    jcenter()
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.4.0")
}

android {
    compileSdkVersion(33)
    defaultConfig {
        applicationId = "com.numq.android"
        minSdkVersion(24)
        targetSdkVersion(33)
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    packagingOptions {
        exclude("META-INF/{AL2.0,LGPL2.1}")
        exclude("META-INF/native-image/**")
    }
}