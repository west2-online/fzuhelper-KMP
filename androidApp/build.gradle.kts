import java.io.FileInputStream
import java.util.Properties

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("com.ncorti.ktfmt.gradle") version "0.20.1"
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

    val configFile = rootProject.file("android-sign.properties")
    val prop = Properties()
    prop.load(FileInputStream(configFile))

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
            keyAlias = prop.getProperty("alias")
            keyPassword = prop.getProperty("keyPassword")
            storeFile = File(prop.getProperty("file"))
            storePassword = prop.getProperty("password")
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

ktfmt {
    googleStyle()
}


