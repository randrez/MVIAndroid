package com.scgts.sctrace.base.model

sealed class UserKey(val key: String) {
    object AccessToken : UserKey("accesstoken")
    object RefreshToken : UserKey("refreshtoken")
    object TokenExpirationDateTime : UserKey("tokenexpirationdatetime")
    object IgnoreTagWarnings : UserKey("ignoretagwarnings")
}