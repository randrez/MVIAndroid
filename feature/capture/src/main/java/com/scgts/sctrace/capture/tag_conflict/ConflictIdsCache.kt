package com.scgts.sctrace.capture.tag_conflict

import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache
import com.scgts.sctrace.base.model.Asset
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class ConflictIdsCache : InMemoryObjectCache<List<Asset>>(listOf()) {
    private val subject = PublishSubject.create<Pair<Asset, String>>()
    private var tagInConflict: String? = null

    fun assetSelectionObs(): Observable<Pair<Asset, String>> = subject.hide()

    fun assetSelected(asset: Asset) : Completable = Completable.fromAction {
        subject.onNext(asset to tagInConflict!!)
    }

    fun setData(assets: List<Asset>, tag: String): Completable {
        tagInConflict = tag
        return this.put(assets)
    }

    fun reset() : Completable {
        tagInConflict = null
        return this.put(emptyList())
    }
}