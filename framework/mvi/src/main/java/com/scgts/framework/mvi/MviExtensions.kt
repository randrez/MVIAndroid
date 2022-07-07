package com.scgts.framework.mvi

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

fun <I : MviIntent, VS: MviViewState> MviViewModel<I, VS>.intentsBuild(
    intentObservable: Observable<I>,
    bindBlock: IntentStream.Binder<I>.() -> Unit
): Observable<I> {
    return IntentStream(
        intentObservable.subscribeOn(Schedulers.io()),
        tag = this.javaClass.name
    )
        .bind(bindBlock)
        .build()
}
