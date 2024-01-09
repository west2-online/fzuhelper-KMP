plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget()
    val appyx_version = "2.0.0-alpha09"
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation("com.bumble.appyx:appyx-navigation-android:$appyx_version")
                implementation("com.bumble.appyx:appyx-interactions-android:$appyx_version")
                implementation("com.bumble.appyx:backstack-android:$appyx_version")
                implementation("com.bumble.appyx:spotlight-android:$appyx_version")
            }
        }
    }
}

android {
    compileSdk = 34
    namespace = "com.myapplication"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "com.myapplication.MyApplication"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
//    buildTypes {
//        debug {
//            initWith debug
//            matchingFallbacks = ['debug']
//        }
//    }

}

