plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("dev.icerock.mobile.multiplatform-resources")
    kotlin("plugin.serialization") version "1.9.20"
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
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
            baseName = "MultiPlatformLibrary"
            export("dev.icerock.moko:resources:0.23.0")
            export("dev.icerock.moko:graphics:0.9.0") // toUIColor here
            export("dev.icerock.moko:mvvm-core:0.16.1")
            export("dev.icerock.moko:mvvm-livedata:0.16.1")
            export("dev.icerock.moko:mvvm-livedata-resources:0.16.1")
            export("dev.icerock.moko:mvvm-state:0.16.1")
        }
    }
    val koin_version = "3.5.0"
    val koin_compose_version = "1.1.0"
    val ktor_version = "2.3.5"
    val serialization_version = "1.6.0"
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
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-client-logging:$ktor_version")
                api("com.rickclephas.kmm:kmm-viewmodel-core:1.0.0-ALPHA-15")

                //koin
                implementation("io.insert-koin:koin-core:$koin_version")
                implementation("io.insert-koin:koin-compose:$koin_compose_version")

                //权限管理
                api("dev.icerock.moko:permissions:0.16.0")

                // compose multiplatform
                api("dev.icerock.moko:permissions-compose:0.16.0") // permissions api + compose extensions
                implementation("dev.icerock.moko:permissions-test:0.16.0")
                api("dev.icerock.moko:resources:0.23.0")
                api("dev.icerock.moko:resources-compose:0.23.0") // for compose multiplatform
                api("dev.icerock.moko:resources-test:0.23.0")


                api("dev.icerock.moko:mvvm-core:0.16.1") // only ViewModel, EventsDispatcher, Dispatchers.UI
                api("dev.icerock.moko:mvvm-flow:0.16.1") // api mvvm-core, CFlow for native and binding extensions
                api("dev.icerock.moko:mvvm-livedata:0.16.1") // api mvvm-core, LiveData and extensions
                api("dev.icerock.moko:mvvm-state:0.16.1") // api mvvm-livedata, ResourceState class and extensions
                api("dev.icerock.moko:mvvm-livedata-resources:0.16.1") // api mvvm-core, moko-resources, extensions for LiveData with moko-resources
                api("dev.icerock.moko:mvvm-flow-resources:0.16.1")
                api("dev.icerock.moko:mvvm-compose:0.16.1") // api mvvm-core, getViewModel for Compose Multiplatfrom
                api("dev.icerock.moko:mvvm-flow-compose:0.16.1") // api mvvm-flow, binding extensions for Compose Multiplatfrom
                api("dev.icerock.moko:mvvm-livedata-compose:0.16.1") // api mvvm-livedata, binding extensions for Compose Multiplatfrom

                //ktor
                implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

                implementation( "org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")

                implementation("com.liftric:kvault:1.12.0")

                // QRcode 生成周期
                implementation("io.github.alexzhirkevich:qrose:1.0.0-beta02")

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                //分页
                implementation("app.cash.paging:paging-common:3.3.0-alpha02-0.4.0")
                implementation("app.cash.paging:paging-compose-common:3.3.0-alpha02-0.4.0")

                implementation(compose.material3)

                implementation("com.doist.x:normalize:1.0.5")

                //加密库
                implementation("dev.whyoleg.cryptography:cryptography-core:0.2.0")
//                //webview
                implementation("io.github.kevinnzou:compose-webview-multiplatform:1.7.4")
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
                implementation("io.ktor:ktor-client-okhttp:$ktor_version")

                //koin
                implementation("io.insert-koin:koin-android:$koin_version")

                api("dev.icerock.moko:mvvm-livedata-material:0.16.1") // api mvvm-livedata, Material library android extensions
                api("dev.icerock.moko:mvvm-livedata-glide:0.16.1") // api mvvm-livedata, Glide library android extensions
                api("dev.icerock.moko:mvvm-livedata-swiperefresh:0.16.1") // api mvvm-livedata, SwipeRefreshLayout library android extensions
                api("dev.icerock.moko:mvvm-databinding:0.16.1") // api mvvm-livedata, DataBinding support for Android
                api("dev.icerock.moko:mvvm-viewbinding:0.16.1") // api mvvm-livedata, ViewBinding support for Android
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
                implementation("io.ktor:ktor-client-darwin:$ktor_version")
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

multiplatformResources {
    multiplatformResourcesPackage = "org.example.library" // required
//    multiplatformResourcesClassName = "SharedRes" // optional, default MR
//    multiplatformResourcesVisibility = MRVisibility.Internal // optional, default Public
//    iosBaseLocalizationRegion = "en" // optional, default "en"
//    multiplatformResourcesSourceSet = "commonClientMain"  // optional, default "commonMain"
}

