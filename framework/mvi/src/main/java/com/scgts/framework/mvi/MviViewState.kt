package com.scgts.framework.mvi

interface MviViewState {
    val error: Throwable?
    val loading: Boolean
}
