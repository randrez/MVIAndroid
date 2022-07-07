package util

import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface ScanStateManager {
    fun scanStateObs(): Observable<ScanState>
    fun autoScanStateObs(): Observable<Boolean>
    fun setScanning(): Completable
    fun setPause(): Completable
    fun turnCameraOff(): Completable
    fun toggleCapture(): Completable
    fun toggleAutoScan(): Completable
}

class ScanStateManagerImpl : ScanStateManager {
    private val scanStateCache = InMemoryObjectCache(ScanState.STANDBY)
    private val autoScanStateCache = InMemoryObjectCache(false)

    private fun setScanState(state: ScanState) =
        scanStateCache.get().flatMapCompletable { currentScanState ->
            if (currentScanState != state) scanStateCache.put(state)
            else Completable.complete()
        }

    override fun scanStateObs() = scanStateCache.getObservable()

    override fun autoScanStateObs(): Observable<Boolean> = autoScanStateCache.getObservable()

    override fun setScanning(): Completable =
        autoScanStateCache.get().flatMapCompletable { autoScanIsOn ->
            if (autoScanIsOn) setScanState(ScanState.SCANNING)
            else setScanState(ScanState.STANDBY)
        }

    override fun setPause(): Completable = setScanState(ScanState.PAUSED)

    override fun turnCameraOff(): Completable = setScanState(ScanState.OFF)

    override fun toggleCapture(): Completable =
        scanStateCache.get().flatMapCompletable { scanState ->
            scanStateCache.put(
                if (scanState == ScanState.SCANNING) ScanState.STANDBY
                else ScanState.SCANNING
            )
        }

    override fun toggleAutoScan(): Completable =
        autoScanStateCache.get().flatMapCompletable {
            autoScanStateCache.put(!it).andThen(setScanning())
        }
}

enum class ScanState {
    STANDBY, SCANNING, PAUSED, OFF
}