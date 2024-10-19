
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

val appyx_version = "2.0.0-alpha09"
val koin_version = "3.5.0"
val koin_compose_version = "1.1.0"
val ktor_version = "2.3.12"
val serialization_version = "1.6.2"
val markdown_version = "0.10.0"
val sqlDelightVersion = "2.0.1"
val voyagerVersion = "1.0.0"

plugins {
    id("org.jetbrains.dokka") version "1.9.20"
    val sqlDelightVersion = "2.0.1"
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("dev.icerock.mobile.multiplatform-resources")
    kotlin("plugin.serialization") version "1.9.20"
    id("kotlin-parcelize")
    id("app.cash.sqldelight") version sqlDelightVersion
    id("com.ncorti.ktfmt.gradle") version "0.20.1"
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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.components.uiToolingPreview)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(compose.material3)
                //图像
                implementation("media.kamel:kamel-image:0.9.4")
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

                //日期
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0-RC")

                //分页
                implementation("app.cash.paging:paging-common:3.3.0-alpha02-0.4.0")
                implementation("app.cash.paging:paging-compose-common:3.3.0-alpha02-0.4.0")



                implementation("com.doist.x:normalize:1.0.5")

                //加密库
                implementation("dev.whyoleg.cryptography:cryptography-core:0.2.0")

//              //webview
                implementation("io.github.kevinnzou:compose-webview-multiplatform:1.9.20")

                implementation("com.mikepenz:multiplatform-markdown-renderer:${markdown_version}")

                //appyx导航
                implementation("com.bumble.appyx:appyx-navigation:$appyx_version")
                implementation("com.bumble.appyx:appyx-interactions:$appyx_version")
                implementation("com.bumble.appyx:backstack:$appyx_version")
                implementation("com.bumble.appyx:spotlight:$appyx_version")

                // Voyager
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")

                //sqlDelight
                implementation("app.cash.sqldelight:coroutines-extensions:$sqlDelightVersion")
                implementation("app.cash.sqldelight:coroutines-extensions:$sqlDelightVersion")

//                //加密
//                implementation("com.ionspin.kotlin:multiplatform-crypto-libsodium-bindings:0.9.1")
                implementation("dev.whyoleg.cryptography:cryptography-serialization-pem:0.3.0")

                val latest_release = "Beta-0.0.5"
                implementation("io.github.thechance101:chart:$latest_release")

                val ksoup_version = "0.1.2"
                implementation("com.fleeksoft.ksoup:ksoup:$ksoup_version")

                val androidxRoom = "2.7.0-alpha01"
                val sqlite = "2.5.0-SNAPSHOT"

//                implementation("androidx.room:room-gradle-plugin:$androidxRoom")
//                implementation("androidx.room:room-compiler:$androidxRoom")
//                implementation("androidx:room:room-runtime:$androidxRoom")
//                implementation("androidx:sqlite:sqlite-bundled:$sqlite")
                implementation("io.github.koalaplot:koalaplot-core:0.6.0")
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                api("androidx.activity:activity-compose:1.9.0-beta01")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
                implementation("io.ktor:ktor-client-okhttp:$ktor_version")
                //koin
                implementation("io.insert-koin:koin-android:$koin_version")
                api("dev.icerock.moko:mvvm-livedata-material:0.16.1") // api mvvm-livedata, Material library android extensions
//                api("dev.icerock.moko:mvvm-livedata-glide:0.16.1") // api mvvm-livedata, Glide library android extensions
                api("dev.icerock.moko:mvvm-livedata-swiperefresh:0.16.1") // api mvvm-livedata, SwipeRefreshLayout library android extensions
                api("dev.icerock.moko:mvvm-databinding:0.16.1") // api mvvm-livedata, DataBinding support for Android
                api("dev.icerock.moko:mvvm-viewbinding:0.16.1") // api mvvm-livedata, ViewBinding support for Android

                implementation("com.bumble.appyx:appyx-navigation-android:$appyx_version")
                implementation("com.bumble.appyx:appyx-interactions-android:$appyx_version")
                implementation("com.bumble.appyx:backstack-android:$appyx_version")
                implementation("com.bumble.appyx:spotlight-android:$appyx_version")
                implementation("app.cash.sqldelight:android-driver:$sqlDelightVersion")
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
                implementation("app.cash.sqldelight:native-driver:$sqlDelightVersion")
                implementation("io.ktor:ktor-client-darwin:$ktor_version")
            }
        }
        val commonTest by getting{
//            dependsOn(commonMain)
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
    task("testClasses")
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
    }
}


android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.fzu.futalk.common"

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

sqldelight {
    databases {
        create("FuTalkDatabase") {
            packageName.set("com.futalk.kmm")
        }
    }
}

ktfmt {
    googleStyle()
}



