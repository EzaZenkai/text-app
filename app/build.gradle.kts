plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "lt.teksto.skaiciuokle"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "lt.teksto.skaiciuokle"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            all {
                it.jvmArgs("-Dnet.bytebuddy.experimental=true")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity.ktx)
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.14.2")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
