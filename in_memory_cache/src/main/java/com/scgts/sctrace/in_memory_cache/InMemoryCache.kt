package com.scgts.sctrace.in_memory_cache

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.ConcurrentHashMap

class InMemoryCache<V> {

    private val concurrentMap = ConcurrentHashMap<String, V>()
    private val cacheSubject = BehaviorSubject.create<Map<String, V>>()

    fun isEmpty() = Single.just(concurrentMap.isEmpty())

    fun put(value: V, id: String): Completable =
        Completable.fromCallable {
            concurrentMap[id] = value
            cacheSubject.onNext(concurrentMap)
        }

    fun putSingle(value: V, id: String): Single<V> =
        Completable.fromCallable {
            concurrentMap[id] = value
            cacheSubject.onNext(concurrentMap)
        }.andThen(Single.just(value))

    fun getObservable(): Observable<List<V>> =
        cacheSubject.hide()
            .map { it.values.toList() }

    fun getValueSingle(id: String): Single<V> =
        Single.just(concurrentMap)
            .flatMap { map ->
                map[id]?.let {
                    Single.just(it)
                } ?: Single.error(NoSuchElementException())
            }

    fun clearAll(): Completable =
        Completable.fromCallable {
            concurrentMap.clear()
        }
}
