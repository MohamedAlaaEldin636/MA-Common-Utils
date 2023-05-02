@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
}

group = "com.github.MohamedAlaaEldin636"
version = "1.6.0"

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.MohamedAlaaEldin636"
            artifactId = "ma-common-utils-core"
            version = "1.6.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

android {
    namespace = "ma.ya.core"

    compileSdk = 33

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")

        aarMetadata {
            minCompileSdk = 33
        }
    }

    //https://developer.android.com/build/publish-library/configure-pub-variants
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
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
    }

    /*
    https://developer.android.com/build/publish-library/prep-lib-release

    testFixtures {
        enable = true
    }
     */
}

dependencies {
    // Androidx
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // UI ( Material design guidelines )
    implementation("com.google.android.material:material:1.8.0")

    // Gson ( Json Serialization & Deserialization )
    implementation("com.google.code.gson:gson:2.10.1")

    // Google Services
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // ---- Testing ---- //

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}