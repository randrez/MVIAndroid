object Version {
    const val apollo = "2.4.3"
    const val dynatrace = "8.223.1.1003"

    const val kotlin = "1.5.21"
    const val kotlinAndroidX = "1.2.0"
    const val constraintLayout = "2.0.4"
    const val swipeRereshLayout = "1.0.0"

    const val appCompat = "1.3.1"
    const val cardView = "1.0.0"
    const val androidMaterial = "1.3.0-beta01"
    const val junit = "4.12"
    const val gsonConverter = "2.4.0"

    const val junitAndroidX = "1.1.2"
    const val rxJava3 = "3.0.0"

    const val retrofitRxAdapter = "3.0.0"
    const val retrofit2 = "2.6.1"

    const val okHttpLogging = "4.2.1"
    const val timber = "5.0.1"
    const val koptional = "1.7.0"
    const val room = "2.3.0-beta01"
    const val threeTen = "1.2.4"

    const val compose = "1.0.1"
    const val activityCompose = "1.3.0"
    const val navigationCompose = "2.4.0-alpha06"
    const val composeSwipeRefresh = "0.18.0"

    const val navigation = "2.3.0"

    const val koin = "2.2.3"

    const val mlKit_barcodeScanning = "16.0.3"

    const val epoxy = "4.2.0"

    const val rxBinding = "4.0.0"

    const val mockK = "1.11.0"

    const val scandit = "6.8.0"

    const val appCenter = "4.1.0"

    const val okta = "1.0.19"
}

object Dependencies {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}"
    const val ktxCore = "androidx.core:core-ktx:${Version.kotlinAndroidX}"

    const val appCompat = "androidx.appcompat:appcompat:${Version.appCompat}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Version.constraintLayout}"
    const val swipeRereshLayout =
        "androidx.swiperefreshlayout:swiperefreshlayout:${Version.swipeRereshLayout}"
    const val cardView = "androidx.cardview:cardview:${Version.cardView}"
    const val androidMaterial = "com.google.android.material:material:${Version.androidMaterial}"
    const val navigation_fragment =
        "androidx.navigation:navigation-fragment-ktx:${Version.navigation}"
    const val navigation_ui_ktx = "androidx.navigation:navigation-ui-ktx:${Version.navigation}"

    const val junit = "junit:junit:${Version.junit}"
    const val junitAndroidX = "androidx.test.ext:junit:${Version.junitAndroidX}"

    const val rxJava3 = "io.reactivex.rxjava3:rxjava:${Version.rxJava3}"

    const val retrofitRxAdapter =
        "com.github.akarnokd:rxjava3-retrofit-adapter:${Version.retrofitRxAdapter}"
    const val retrofit2 = "com.squareup.retrofit2:retrofit:${Version.retrofit2}"
    const val retrofit_gsonConverter =
        "com.squareup.retrofit2:converter-gson:${Version.gsonConverter}"

    const val rxKotlin3 = "io.reactivex.rxjava3:rxkotlin:${Version.rxJava3}"
    const val rxAndroid3 = "io.reactivex.rxjava3:rxandroid:${Version.rxJava3}"

    const val timber = "com.jakewharton.timber:timber:${Version.timber}"

    const val koptional = "com.gojuno.koptional:koptional:${Version.koptional}"
    const val koptional_rxjava3 =
        "com.gojuno.koptional:koptional-rxjava3-extensions:${Version.koptional}"

    const val okhttp_logging = "com.squareup.okhttp3:logging-interceptor:${Version.okHttpLogging}"

    const val apolloRuntime = "com.apollographql.apollo:apollo-runtime:${Version.apollo}"
    const val apolloRx = "com.apollographql.apollo:apollo-rx3-support:${Version.apollo}"
    const val apolloAndroidSupport =
        "com.apollographql.apollo:apollo-android-support:${Version.apollo}"

    const val koin_core = "io.insert-koin:koin-core:${Version.koin}"
    const val koinAndroid = "io.insert-koin:koin-android:${Version.koin}"
    const val koinScope = "io.insert-koin:koin-android-scope:${Version.koin}"
    const val koinViewModel = "io.insert-koin:koin-android-viewmodel:${Version.koin}"
    const val koinTest = "io.insert-koin:koin-test:${Version.koin}"

    const val mockK = "io.mockk:mockk:${Version.mockK}"

    const val mlKit_barcodeScanning =
        "com.google.mlkit:barcode-scanning:${Version.mlKit_barcodeScanning}"

    const val room = "androidx.room:room-runtime:${Version.room}"
    const val room_ktx = "androidx.room:room-ktx:${Version.room}"
    const val room_rxjava3 = "androidx.room:room-rxjava3:${Version.room}"
    const val room_compiler = "androidx.room:room-compiler:${Version.room}"
    const val threeTenBp = "com.jakewharton.threetenabp:threetenabp:${Version.threeTen}"
    const val threeTenBp = "com.jakewharton.threetenabp:threetenabp:${Version.threeTen}"

    const val compose = "androidx.compose.ui:ui:${Version.compose}"
    const val composeMaterial = "androidx.compose.material:material:${Version.compose}"
    const val composeUITooling = "androidx.compose.ui:ui-tooling:${Version.compose}"
    const val activityCompose = "androidx.activity:activity-compose:${Version.activityCompose}"
    const val navigationCompose =
        "androidx.navigation:navigation-compose:${Version.navigationCompose}"
    const val composeLivedata = "androidx.compose.runtime:runtime-livedata:${Version.compose}"
    const val composeSwipeRefresh =
        "com.google.accompanist:accompanist-swiperefresh:${Version.composeSwipeRefresh}"
    const val junitAndroidXCompose = "androidx.compose.ui:ui-test-junit4:${Version.compose}"

    const val epoxy = "com.airbnb.android:epoxy:${Version.epoxy}"
    const val epoxyAnnotation = "com.airbnb.android:epoxy-processor:${Version.epoxy}"

    const val rxBinding = "com.jakewharton.rxbinding4:rxbinding:${Version.rxBinding}"

    const val scandit = "com.scandit.datacapture:core:${Version.scandit}"
    const val scanditBarcode = "com.scandit.datacapture:barcode:${Version.scandit}"

    const val appCenter = "com.microsoft.appcenter:appcenter-distribute:${Version.appCenter}"

    const val okta = "com.okta.android:okta-oidc-android:${Version.okta}"
}

object BuildVersion {
    const val compileSdk = 30
    const val minSdk = 24
    const val targetSdk = 30
    const val buildTools = "30.0.2"
    const val versionCode = 10
    const val versionName = "1.2.3"
}

object GradleTemplates {
    const val data_source = "base_datasource.gradle"
    const val feature = "base_feature.gradle"
}
