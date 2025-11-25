package com.miapp.greenbunny.api

import com.miapp.greenbunny.BuildConfig

/**
 * ApiConfig centraliza la lectura de las URLs base desde BuildConfig.
 */
object ApiConfig {
    val storeBaseUrl: String = BuildConfig.XANO_STORE_BASE
    val authBaseUrl: String = BuildConfig.XANO_AUTH_BASE
}
