package com.scgts.sctrace.framework.view

import com.scgts.framework.mvi.MviViewState

interface MviFragment<VS : MviViewState> {
    fun render(viewState: VS)
}
