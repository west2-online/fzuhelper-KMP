plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"

}
buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://jogamp.org/deployment/maven")
    }

    dependencies {
        classpath ("dev.icerock.moko:resources-generator:0.23.0")
    }
}




tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}