package util

import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

interface TopToastManager {
    fun listenForToast(): Observable<String>
    fun showToast(message: String): Completable
    fun setShowAssetAddedToastFlag(show: Boolean): Completable
    fun showAssetAddedToast(): Completable
    fun hideAssetAddedToast(): Completable
    fun getShowAssetAddedToastObs(): Observable<Boolean>
}

class TopToastManagerImpl : TopToastManager {
    private var showAssetAddedToastFlag = false
    private val showAssetAddedToast = InMemoryObjectCache(false)

    private val subject: PublishSubject<String> = PublishSubject.create<String>()

    override fun listenForToast(): Observable<String> {
        return subject.hide()
    }

    override fun showToast(message: String): Completable {
        subject.onNext(message)
        return Completable.complete()
    }

    override fun setShowAssetAddedToastFlag(show: Boolean): Completable {
        showAssetAddedToastFlag = show
        return Completable.complete()
    }

    override fun showAssetAddedToast(): Completable {
        return if (showAssetAddedToastFlag) showAssetAddedToast.put(true)
        else Completable.complete()
    }

    override fun hideAssetAddedToast(): Completable {
        return showAssetAddedToast.put(false).andThen(setShowAssetAddedToastFlag(false))
    }

    override fun getShowAssetAddedToastObs(): Observable<Boolean> {
        return showAssetAddedToast.getObservable()
    }
}