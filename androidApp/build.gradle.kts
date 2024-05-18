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
    namespace = "com.fzu.futalk"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "com.fzu.futalk"
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
    signingConfigs {
        create("release") {
            storeFile = file("futalk.jks")
            storePassword = "futalk"
            keyAlias = "futalk"
            keyPassword = "futalk"
        }
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("release")
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
//    buildTypes {
//        debug {
//            initWith debug
//            matchingFallbacks = ['debug']
//        }
//    }

}

