package com.scgts.framework.mvi

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.merge
import kotlin.reflect.KClass

class IntentStream<I : Any> internal constructor(
    intentsSource: Observable<I>,
    private val tag: String
) {

    private val viewSource: Observable<I> = intentsSource.share()
    private val dataSinks = mutableListOf<Observable<I>>()
    private val viewSinks = mutableListOf<Observable<I>>()
    private val internalBuilder = Binder(viewSource, dataSinks, viewSinks, tag)

    fun build(): Observable<I> {
        return Observable.merge(buildView(), buildData())
    }

    internal fun bind(block: Binder<I>.() -> Unit): IntentStream<I> {
        internalBuilder.block()
        return this
    }

    private fun buildView(): Observable<I> {
        return Observable.merge(viewSinks)
    }

    private fun buildData(): Observable<I> {
        return Observable.merge(dataSinks)
    }

    class Binder<I : Any> internal constructor(
        private val viewSource: Observable<I>,
        private val dataSinks: MutableList<Observable<I>>,
        private val viewSinks: MutableList<Observable<I>>,
        private val tag: String
    ) {
        //Passthrough these view intents straight to reducer
        fun viewIntentPassThroughs(vararg clazz: KClass<out I>) {
            viewSinks.add(clazz.map { viewSource.ofType(it.java) }.merge())
        }

        fun dataIntent(completable: Completable) {
            dataSinks.add(completable.toObservable())
        }

        fun dataIntent(obs: Observable<I>) {
            dataSinks.add(obs)
        }

        fun <T> dataIntent(obs: Observable<T>, block: (Observable<T>) -> Observable<I>) {
            dataSinks.add(block(obs))
        }

        fun <T> dataIntent(single: Single<T>, block: (Single<T>) -> Observable<I>) {
            dataSinks.add(block(single))
        }

        fun dataIntent(completable: Completable, block: (Completable) -> Observable<I>) {
            dataSinks.add(block(completable))
        }

        inline fun <reified T> viewIntentObservable(noinline block: (Observable<T>) -> Observable<I>) {
            viewIntentObs(T::class.java, block)
        }

        inline fun <reified T> viewIntentCompletable(noinline block: (Observable<T>) -> Completable) {
            viewIntentComplete(T::class.java, block)
        }

        @PublishedApi
        internal fun <T> viewIntentObs(clazz: Class<T>, block: (Observable<T>) -> Observable<I>) {
            view(on(clazz, block))
        }

        @PublishedApi
        internal fun <T> viewIntentComplete(
            clazz: Class<T>,
            block: (Observable<T>) -> Completable
        ) {
            view(onComplete(clazz, block))
        }

        private fun <T> on(
            clazz: Class<T>,
            block: Observable<T>.() -> Observable<I>
        ): Observable<I> {
            return viewSource.ofType(clazz).block()
        }

        private fun <T> onComplete(
            clazz: Class<T>,
            block: Observable<T>.() -> Completable
        ): Observable<I> {
            return viewSource.ofType(clazz).block()
                .toObservable()
        }

        private fun view(obs: Observable<I>) {
            viewSinks.add(obs)
        }
    }
}
