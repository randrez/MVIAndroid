package com.scgts.framework.mvi

import io.reactivex.rxjava3.core.Observable

interface MviViewModel<T: MviIntent, VS : MviViewState> {
    fun bind(intents : Observable<T>) : Observable<VS>
}
