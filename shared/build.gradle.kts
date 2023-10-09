plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }
    val koin_version = "3.5.0"
    val koin_android_version = "3.5.0"
    val koin_android_compose_version = "3.5.0"
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                //图像
                implementation("media.kamel:kamel-image:0.7.3")
                implementation("io.ktor:ktor-client-core:2.3.4")
                api("com.rickclephas.kmm:kmm-viewmodel-core:1.0.0-ALPHA-14")
                implementation("io.insert-koin:koin-core:$koin_version")
                implementation("io.insert-koin:koin-test:$koin_version")
                api("dev.icerock.moko:mvvm-core:0.13.1")

                //权限管理
                api("dev.icerock.moko:permissions:0.16.0")
                // compose multiplatform
                api("dev.icerock.moko:permissions-compose:0.16.0") // permissions api + compose extensions
                implementation("dev.icerock.moko:permissions-test:0.16.0")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
                implementation("io.ktor:ktor-client-okhttp:2.3.4")
                implementation("io.insert-koin:koin-android:$koin_android_version")
                implementation("io.insert-koin:koin-androidx-compose:$koin_android_compose_version")
                implementation("io.insert-koin:koin-androidx-compose:3.4.2")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.4")
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.myapplication.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}
