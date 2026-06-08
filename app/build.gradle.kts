plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "jdc.trabajos.smarthelper"
    compileSdk = 34

    defaultConfig {
        applicationId = "jdc.trabajos.smarthelper"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.preference)

    // ROOM
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // MAPS Y GEOLOCALIZACIÓN
    implementation(libs.play.services.maps)
    implementation(libs.maps)
    implementation(libs.location)

    // NAVIGATION
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}")
        }
    }
}