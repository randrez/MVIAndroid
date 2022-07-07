package com.scgts.sctrace.see_details.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.scgts.sctrace.framework.view.BaseFragment
import com.scgts.sctrace.see_details.R
import com.scgts.sctrace.see_details.databinding.FragmentSeeDetailsBinding
import org.koin.android.viewmodel.ext.android.viewModel
import com.scgts.sctrace.see_details.ui.SeeDetailsMvi.Intent
import com.scgts.sctrace.see_details.ui.SeeDetailsMvi.ViewState
import com.scgts.sctrace.ui.components.OrderDetailsControllerInput
import com.scgts.sctrace.ui.components.OrderDetailsEpoxyController
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.core.parameter.parametersOf
import util.sendErrorToDtrace

class SeeDetailsFragment : BaseFragment<SeeDetailsViewModel>() {

    private val intents = PublishSubject.create<Intent>()

    private val args: SeeDetailsFragmentArgs by navArgs()

    override val viewModel: SeeDetailsViewModel by viewModel {
        parametersOf(args)
    }

    private lateinit var binding: FragmentSeeDetailsBinding

    private val controller by lazy {
        OrderDetailsEpoxyController { row ->
            intents.onNext(Intent.ToggleRowExpanded(row))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.bind(intents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::render){error:Throwable -> error.sendErrorToDtrace(this.javaClass.name)}
            .autoDisposeOnDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeeDetailsBinding.inflate(inflater, container, false)

        binding.recycler.run {
            layoutManager = LinearLayoutManager(context)
            adapter = controller.adapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
    }

    private fun render(viewState: ViewState) {
        controller.unitType = viewState.unitType
        controller.setData(
            OrderDetailsControllerInput(
                specialInstructions = viewState.task?.specialInstructions,
                instructionsExpanded = viewState.instructionsExpanded,
                assetsExpanded = viewState.assetsExpanded,
                info = viewState.assetProductDescription,
                hideInstructions = viewState.hideInstructions,
                isDispatchOrBuildOrder = viewState.isDispatchOrBuildOrder
            )
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> intents.onNext(Intent.XClicked)
        }
        return super.onOptionsItemSelected(item)
    }
}
