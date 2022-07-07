package com.scgts.sctrace.in_memory_cache

import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.gojuno.koptional.rxjava3.filterSome
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

open class InMemoryObjectCache<V : Any> constructor(){

    constructor(default: V) : this() {
        cacheSubject.onNext(Some(default))
    }

    private val cacheSubject = BehaviorSubject.createDefault<Optional<V>>(None)

    fun isEmpty(): Single<Boolean> = cacheSubject.first(None).map { it is None }

    fun isEmptyObs() : Observable<Boolean> = cacheSubject.defaultIfEmpty(None).map { it is None }

    open fun put(value: V): Completable {
        return Completable.fromCallable {
            postValue(value)
        }
    }

    fun putSingle(value: V): Single<V> {
        return Completable.fromCallable {
            cacheSubject.onNext(Some(value))
        }.andThen(Single.just(value))
    }

    fun edit(block: V.() -> (V)): Completable {
        return get().flatMapCompletable {
            put(it.block())
        }
    }

    fun get(): Single<V> {
        return cacheSubject.first(None).map {
            if (it is Some) {
                it.value
            } else {
                throw NoSuchElementException("Cache is empty")
            }
        }
    }

    fun getObservable(): Observable<V> {
        return cacheSubject.hide()
            .filterSome()
    }

    fun clear() = Completable.fromCallable {
        postValue(null)
    }

    @Synchronized
    private fun postValue(value: V?) {
        if (value != null) {
            cacheSubject.onNext(Some(value))
        } else {
            cacheSubject.onNext(None)
        }
    }
}

fun <T, V : Any> InMemoryObjectCache<V>.ifEmptyElse(ifBlock: () -> Single<T>, elseBlock: () -> Single<T>): Single<T> {
    return isEmpty().flatMap { isEmpty ->
        if(isEmpty) {
            ifBlock()
        } else {
            elseBlock()
        }
    }
}

fun <V : Any> InMemoryObjectCache<V>.fillWithOrGet(fillBlock: () -> Single<V>): Single<V> {
    return isEmpty().flatMap { isEmpty ->
        if(isEmpty) {
            fillBlock().flatMapCompletable { put(it) }.andThen(get())
        } else {
            get()
        }
    }
}
