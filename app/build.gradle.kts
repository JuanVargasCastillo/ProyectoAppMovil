plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.miapp.greenbunny"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.miapp.greenbunny"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "XANO_STORE_BASE", "\"https://x8ki-letl-twmt.n7.xano.io/api:Ybbgn3cq/\"")
        buildConfigField("String", "XANO_AUTH_BASE", "\"https://x8ki-letl-twmt.n7.xano.io/api:iHS4Ivne/\"")
        buildConfigField("int", "XANO_TOKEN_TTL_SEC", "86400")
    }

    // ⭐ SOLO SE AGREGA ESTA PARTE ⭐
    signingConfigs {
        create("release") {
            storeFile = file("C:/Users/matii/greenbunny-release.jks")
            storePassword = "Holamundo123"
            keyAlias = "greenbunny_key"
            keyPassword = "Holamundo123"
        }
    }
    // ⭐ FIN DE LA PARTE AGREGADA ⭐

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // ⭐ SOLO SE AGREGA ESTA LÍNEA ⭐
            signingConfig = signingConfigs.getByName("release")
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.coil)
    implementation(libs.androidx.activity)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
