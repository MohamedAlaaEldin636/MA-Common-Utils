@file:Suppress("UnstableApiUsage")

import com.android.build.api.variant.BuildConfigField

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

private val fileProviderAndroidManifestXmlAuthority = "ma.ya.macommonutils.fileprovider"

/*androidComponents {
    onVariants {
        it.buildConfigFields.put(
            "fileProviderAndroidManifestXmlAuthority", BuildConfigField(
                "String", "\"${fileProviderAndroidManifestXmlAuthority}\"", ""
            )
        )
    }
}*/

android {
    namespace = "ma.ya.macommonutils"

    compileSdk = 33

    defaultConfig {
        applicationId = "ma.ya.macommonutils"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val fileProviderAndroidManifestXmlAuthority = "ma.ya.macommonutils.fileprovider"

        manifestPlaceholders += "fileProviderAndroidManifestXmlAuthority" to fileProviderAndroidManifestXmlAuthority
        buildConfigField("String", "fileProviderAndroidManifestXmlAuthority", "\"${fileProviderAndroidManifestXmlAuthority}\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        //compose = true
        buildConfig = true
    }
}

dependencies {
    // Local Libraries
    implementation(project(":Core"))

    // AndroidX
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // UI ( Material design guidelines )
    implementation("com.google.android.material:material:1.8.0")

    // Image handler ( Glide )
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // ---- Testing ---- //

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}