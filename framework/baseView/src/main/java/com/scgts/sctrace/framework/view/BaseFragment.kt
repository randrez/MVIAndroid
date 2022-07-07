package com.scgts.sctrace.framework.view

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class  BaseFragment<T : ViewModel> : Fragment() {

    protected abstract val viewModel : T

    private val viewModelDisposable = CompositeDisposable()

    protected fun Disposable.autoDisposeOnDestroy() {
            viewModelDisposable.addAll(this)
    }

    override fun onDestroy() {
        viewModelDisposable.clear()
        super.onDestroy()
    }
}
