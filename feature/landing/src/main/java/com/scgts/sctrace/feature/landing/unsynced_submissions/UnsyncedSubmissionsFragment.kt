package com.scgts.sctrace.feature.landing.unsynced_submissions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.feature.landing.databinding.FragmentUnsyncedSubmissionsBinding
import com.scgts.sctrace.feature.landing.unsynced_submissions.UnsyncedSubmissionMvi.Intent.OnBackPressed
import com.scgts.sctrace.feature.landing.unsynced_submissions.UnsyncedSubmissionMvi.ViewState
import com.scgts.sctrace.framework.view.BaseFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.android.viewmodel.ext.android.viewModel
import util.sendErrorToDtrace

class UnsyncedSubmissionsFragment : BaseFragment<UnsyncSubmissionViewModel>() {
    override val viewModel: UnsyncSubmissionViewModel by viewModel()
    private val args: UnsyncedSubmissionsFragmentArgs by navArgs()
    private val intents = PublishSubject.create<UnsyncedSubmissionMvi.Intent>()
    private lateinit var binding: FragmentUnsyncedSubmissionsBinding
    private val controller = UnsyncedSubmissionController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.bind(intents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::render){error:Throwable -> error.sendErrorToDtrace(this.javaClass.name)}
            .autoDisposeOnDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentUnsyncedSubmissionsBinding.inflate(inflater, container, false).let {
        binding = it
        it.unsyncRecyclerView.setController(controller)
        it.header.setOnClickListener { intents.onNext(OnBackPressed) }
        it.header.text = args.originName
        it.root
    }

    private fun render(viewState: ViewState) {
        binding.title.text =
            getString(R.string.unsynced_submissions_num, viewState.unsyncedSubmissionList.size)
        controller.setData(viewState.unsyncedSubmissionList)
    }
}