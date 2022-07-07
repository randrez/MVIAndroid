package com.scgts.sctrace

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.microsoft.appcenter.distribute.Distribute
import com.microsoft.appcenter.distribute.DistributeListener
import com.microsoft.appcenter.distribute.ReleaseDetails
import com.microsoft.appcenter.distribute.UpdateAction
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import util.sendErrorToDtrace
import java.util.concurrent.TimeUnit

class ScTraceDistributeListener(private val distributeRepository: DistributeRepository
                                ): DistributeListener {

    private lateinit var context: Context

    fun setContext(context: Context) {this.context = context}

    private val timer = startTimer()

    override fun onNoReleaseAvailable(activity: Activity?) {
        distributeRepository.setStatus(UpdateStatus.NO_UPDATE)
    }

    private fun startTimer(): Disposable {
        return Observable.timer(10, TimeUnit.SECONDS)
            .flatMap { distributeRepository.getStatus() }
            .subscribe({
            if (it == UpdateStatus.NO_STATUS) {
                distributeRepository.setStatus(UpdateStatus.NO_UPDATE)
            }
                timer.dispose()
        }, {e -> e.sendErrorToDtrace(this.javaClass.name)})
    }

    override fun onReleaseAvailable(activity: Activity?, releaseDetails: ReleaseDetails?): Boolean {
        distributeRepository.setStatus(UpdateStatus.UPDATE)
            AlertDialog.Builder(context)
                .setTitle(com.microsoft.appcenter.distribute.R.string.appcenter_distribute_update_dialog_title)
                .setMessage("SC Trace ${releaseDetails?.shortVersion} is available to download and install")
                .setPositiveButton(com.microsoft.appcenter.distribute.R.string.appcenter_distribute_update_dialog_download,
                    DialogInterface.OnClickListener { dialog, which -> Distribute.notifyUpdateAction(UpdateAction.UPDATE) })
                .setCancelable(false)
                .show()

        return true
    }
}