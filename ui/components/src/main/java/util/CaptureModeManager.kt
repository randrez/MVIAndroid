package util

import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface CaptureModeManager {
    fun setAsset(): Completable
    fun setTag(assetId: String): Completable
    fun setConsume(): Completable
    fun setReject(quickReject: Boolean = false): Completable
    fun stateObs(): Observable<CaptureMode>
    fun state(): Single<CaptureMode>
    fun setShowConsumptionDuplicate(consumptionType: CaptureMode.Consumption): Completable
    fun showConsumptionDuplicateObs(): Observable<CaptureMode.Consumption>
}

class CaptureModeManagerImpl : CaptureModeManager {

    private val captureState = InMemoryObjectCache<CaptureMode>(CaptureMode.Assets)
    private val showConsumptionDuplicate =
        InMemoryObjectCache<CaptureMode.Consumption>(CaptureMode.Consumption.Unknown)

    override fun setAsset(): Completable = captureState.get().flatMapCompletable { state ->
        if (state !is CaptureMode.Assets) {
            captureState.put(CaptureMode.Assets)
        } else Completable.complete()
    }

    override fun setTag(assetId: String): Completable =
        captureState.get().flatMapCompletable { state ->
            if (state !is CaptureMode.Tags) {
                captureState.put(CaptureMode.Tags(assetId))
            } else Completable.complete()
        }

    override fun setConsume(): Completable = captureState.get().flatMapCompletable { state ->
        if (state !is CaptureMode.Consumption.Consume) {
            captureState.put(CaptureMode.Consumption.Consume)
        } else Completable.complete()
    }

    override fun setReject(quickReject: Boolean): Completable =
        captureState.get().flatMapCompletable { state ->
            if (state !is CaptureMode.Consumption.Reject) {
                captureState.put(CaptureMode.Consumption.Reject(quickReject))
            } else Completable.complete()
        }

    override fun stateObs(): Observable<CaptureMode> = captureState.getObservable()

    override fun state(): Single<CaptureMode> = captureState.get()

    override fun setShowConsumptionDuplicate(consumptionType: CaptureMode.Consumption): Completable =
        showConsumptionDuplicate.put(consumptionType)

    override fun showConsumptionDuplicateObs(): Observable<CaptureMode.Consumption> =
        showConsumptionDuplicate.getObservable()
}

sealed class CaptureMode {
    object Assets : CaptureMode()
    data class Tags(val assetId: String) : CaptureMode()
    sealed class Consumption : CaptureMode() {
        object Consume : Consumption()
        data class Reject(val quickReject: Boolean = false) : Consumption()
        object Unknown : Consumption()
    }
}