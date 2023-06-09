plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

group = "com.numq"
version = "1.0.0"

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api("org.jetbrains.compose.material:material-icons-extended-desktop:1.1.0")
                api("io.insert-koin:koin-core:3.2.0")

                api("org.bytedeco:javacv:1.5.8") {
                    exclude("org.bytedeco", "artoolkitplus")
                    exclude("org.bytedeco", "openblas")
                    exclude("org.bytedeco", "opencv")
                    exclude("org.bytedeco", "flandmark")
                    exclude("org.bytedeco", "flycapture")
                    exclude("org.bytedeco", "leptonica")
                    exclude("org.bytedeco", "libdc1394")
                    exclude("org.bytedeco", "libfreenect")
                    exclude("org.bytedeco", "libfreenect2")
                    exclude("org.bytedeco", "librealsense")
                    exclude("org.bytedeco", "librealsense2")
                    exclude("org.bytedeco", "tesseract")
                    exclude("org.bytedeco", "videoinput")
                    exclude("org.hamcrest", "hamcrest-core")
                }

                api("org.bytedeco:ffmpeg:5.1.2-1.5.8:android-arm64")
                api("org.bytedeco:ffmpeg:5.1.2-1.5.8:android-x86_64")
                api("org.bytedeco:ffmpeg:5.1.2-1.5.8:windows-x86_64")

                api(project(":encoder"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.4.1")
                api("androidx.core:core-ktx:1.7.0")
                api("androidx.activity:activity-compose:1.4.0")
                api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
                api("androidx.documentfile:documentfile:1.0.1")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdkVersion(33)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(33)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}