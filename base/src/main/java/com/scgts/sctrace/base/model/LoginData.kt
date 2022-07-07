package com.scgts.sctrace.base.model

data class LoginData(
    val accessToken: String,
    val refreshToken: String,
    val tokenExpirationTime: Int,
)
