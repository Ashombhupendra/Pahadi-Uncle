plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize) apply false
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
//    kotlin("kapt")
}

kapt {
    correctErrorTypes = true
}


android {
    namespace = "com.pahadi.uncle"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pahadi.uncle"
        minSdk = 24
        targetSdk = 34
        versionCode = 46
        versionName = "0.4.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }


}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.livedata.lifecycle)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.coordinatorlayout)
    implementation(libs.progressbutton)
    implementation("com.github.aabhasr1:OtpView:v1.1.2-ktx")
//    implementation ("io.nerdythings:okhttp-profiler:1.1.1")
//    implementation("com.github.smarteist:autoimageslider:1.4.0")
    implementation(libs.recyclerview)
    implementation(libs.circularImageView)
    implementation(libs.shimmer)
    implementation(libs.glide)
    implementation(libs.nav.fragment.ktx)
    implementation(libs.nav.ui.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.fast.android.networking)
//    implementation(libs.play.core.ktx)
    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:app-update:2.1.0")

    implementation(libs.paging.runtime.ktx)
    implementation(libs.preference.ktx)
    implementation(libs.room.runtime)
//    kapt(libs.room.compiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.oops.no.internet)
    implementation(libs.dexter)
    implementation(libs.picasso)
    implementation(libs.sweet.alert)
    implementation(libs.lottie)
    implementation(libs.photo.view)
    implementation(libs.shortcut.badger)
    implementation(libs.light.compressor)
    implementation(libs.silicompressor)
    implementation(libs.isoparser)
//    implementation(libs.razorpay)
    implementation(libs.androidx.compose.runtime)

    // Test dependencies
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
}
