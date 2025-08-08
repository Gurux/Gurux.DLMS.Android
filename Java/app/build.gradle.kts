plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "gurux.dlms.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "gurux.dlms.android"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "2.0.16"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.guruxCommonAndroid)
    implementation(libs.guruxSerialAndroid)
    implementation(libs.guruxNetAndroid)
    implementation(project(":DLMS"))
    implementation(project(":UI"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.flexbox)
}