package util

import com.scgts.sctrace.base.auth.SettingsManager
import com.scgts.sctrace.base.model.CaptureMethod
import com.scgts.sctrace.base.model.CaptureMethod.Camera
import com.scgts.sctrace.base.model.CaptureMethod.Unknown
import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface CaptureMethodManager {
    fun captureMethod(): Single<CaptureMethod>
    fun captureMethodObs(): Observable<CaptureMethod>
    fun setCaptureMethod(captureMethod: CaptureMethod): Completable
    fun enterTagMode(): Completable
    fun leaveTagMode(): Completable
    fun resetMethodToDefault(): Completable
}

class CaptureMethodManagerImpl(
    private val settingsManager: SettingsManager,
) : CaptureMethodManager {

    private val currentCaptureMethod =
        InMemoryObjectCache<CaptureMethod>(settingsManager.captureMethod().blockingFirst())

    private val addTagPreviousCaptureMethod = InMemoryObjectCache<CaptureMethod>(Unknown)

    override fun captureMethod(): Single<CaptureMethod> = currentCaptureMethod.get()

    override fun captureMethodObs(): Observable<CaptureMethod> =
        currentCaptureMethod.getObservable()

    override fun setCaptureMethod(captureMethod: CaptureMethod): Completable =
        currentCaptureMethod.get().flatMapCompletable { currentMethod ->
            if (currentMethod != captureMethod) currentCaptureMethod.put(captureMethod)
            else Completable.complete()
        }

    override fun enterTagMode(): Completable =
        currentCaptureMethod.get().flatMapCompletable { currentMethod ->
            addTagPreviousCaptureMethod.put(currentMethod).andThen(currentCaptureMethod.put(Camera))
        }

    override fun leaveTagMode(): Completable =
        addTagPreviousCaptureMethod.get().flatMapCompletable { prevMethod ->
            setCaptureMethod(prevMethod).andThen(addTagPreviousCaptureMethod.put(Unknown))
        }

    override fun resetMethodToDefault(): Completable =
        settingsManager.captureMethod().flatMapCompletable { defaultMethod ->
            setCaptureMethod(defaultMethod)
        }
}