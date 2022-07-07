package com.scgts.sctrace.assets.confirmation.discard

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.scgts.sctrace.assets.confirmation.databinding.FragmentDiscardConfirmationBinding
import com.scgts.sctrace.assets.confirmation.discard.AssetDiscardConfirmationMvi.Intent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import util.sendErrorToDtrace

class AssetDiscardConfirmationFragment : BottomSheetDialogFragment() {
    private val args: AssetDiscardConfirmationFragmentArgs by navArgs()
    private val viewModel by viewModel<AssetDiscardConfirmationViewModel> { parametersOf(args) }
    private val disposables = CompositeDisposable()
    private val intents = PublishSubject.create<Intent>()
    private lateinit var binding: FragmentDiscardConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.bind(intents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::render) { error: Throwable -> error.sendErrorToDtrace(this.javaClass.name) }
            .addTo(disposables)
    }

    private fun render(viewState: AssetDiscardConfirmationMvi.ViewState) {
        if (viewState.dismiss) dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentDiscardConfirmationBinding.inflate(inflater, container, false).let {
        binding = it
        it.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            View.OnClickListener { intents.onNext(Intent.ConfirmDiscardClick) }.also {
                trashCanIcon.setOnClickListener(it)
                trashText.setOnClickListener(it)
            }

            View.OnClickListener { intents.onNext(Intent.CancelDiscardClick) }.also {
                cancelIcon.setOnClickListener(it)
                cancelText.setOnClickListener(it)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        intents.onNext(Intent.OnDismiss)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}