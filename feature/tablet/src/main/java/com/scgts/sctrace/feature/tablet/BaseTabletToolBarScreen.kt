package com.scgts.sctrace.feature.tablet

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.scgts.sctrace.feature.tablet.BaseTabletMvi.Intent
import com.scgts.sctrace.feature.tablet.BaseTabletMvi.ViewState
import com.scgts.sctrace.framework.view.BaseFragment
import com.scgts.sctrace.root.components.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import util.sendErrorToDtrace

abstract class BaseTabletToolBarScreen<V: ViewModel>: BaseFragment<V>() {
    private val toolbarViewModel: BaseTabletViewModel by viewModel()
    private val toolbarIntents = PublishSubject.create<Intent>()
    private val compositeDisposable = CompositeDisposable()
    protected var toolbar: MaterialToolbar? = null

    protected abstract fun getScreenTitle(): String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar?.apply {
            findViewById<ConstraintLayout>(R.id.icon_with_number).setOnClickListener {
                toolbarIntents.onNext(Intent.GoToUnsyncedSubmissions(getScreenTitle()))
            }
            findViewById<ImageView>(R.id.settings).setOnClickListener {
                toolbarIntents.onNext(Intent.GoToSettings(getScreenTitle()))
            }
            findViewById<TextView>(R.id.toolbar_title).text = getScreenTitle()
        }
    }

    protected fun setBackArrow() {
        toolbar?.findViewById<TextView>(R.id.toolbar_title)?.let { titleTextView ->
            titleTextView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_chevron_left, 0,0,0
            )
            titleTextView.setOnClickListener { toolbarIntents.onNext(Intent.GoBack) }
        }
    }

    protected fun hideIcons() {
        toolbar?.apply {
            findViewById<ImageView>(R.id.refresh).isVisible = false
            findViewById<ConstraintLayout>(R.id.icon_with_number).isVisible = false
            findViewById<ImageView>(R.id.settings).isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        toolbarViewModel.bind(toolbarIntents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::baseRender){error:Throwable -> error.sendErrorToDtrace(this.javaClass.name)}
            .addTo(compositeDisposable)
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    private fun baseRender(viewState: ViewState) {
        toolbar?.apply {
            findViewById<TextView>(R.id.number).also { number ->
                number.text = viewState.unsubmittedTaskCount.toString()
                number.isVisible = viewState.unsubmittedTaskCount != 0
            }
        }
    }
}