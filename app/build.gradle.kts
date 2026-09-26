
plugins {
    //basic advanced (compose,room,oss)
    alias(libs.plugins.oss.licenses)
    //basic traditional
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)

}

@Suppress("DEPRECATION")
android {
    namespace = "com.suming.reparacion"
    //编译sdk
    compileSdk = 37

    defaultConfig {
        applicationId = "com.suming.reparacion"
        minSdk = 26
        maxSdk = 37
        //noinspection OldTargetApi
        targetSdk = 36
        versionCode = 1
        versionName = "1.2.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk{
            abiFilters.add("arm64-v8a")
            abiFilters.add("x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    packaging{
        jniLibs{
            useLegacyPackaging = true
        }
        dex{
            useLegacyPackaging = true
        }
        resources{
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("fonts/**")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        aidl = true
    }

    sourceSets {
        named("main") {
            aidl.directories.add("src/main/aidl")
        }
    }

}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {


    //basic advanced (compose,room,oss,bybom)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.play.services.oss.licenses)
    //basic traditional (kotlin,agp,ksp)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.foundation.layout)


}

