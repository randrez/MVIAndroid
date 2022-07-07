package com.scgts.sctrace.assets.confirmation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jakewharton.rxbinding4.widget.queryTextChanges
import com.scgts.sctrace.assets.confirmation.SearchableBottomSheetData.SearchableConditionData
import com.scgts.sctrace.assets.confirmation.SearchableBottomSheetData.SearchableLocationData
import com.scgts.sctrace.assets.confirmation.databinding.FragmentSearchableBottomSheetBinding
import com.scgts.sctrace.base.model.Reason
import com.scgts.sctrace.base.model.Condition
import com.scgts.sctrace.base.model.RackLocation
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import util.isTablet
import java.util.concurrent.TimeUnit

class SearchableBottomSheet(val onItemClicked: (SearchableBottomSheetData) -> Unit) :
    BottomSheetDialogFragment() {

    private val controller by lazy {
        SearchableBottomSheetController {
            onItemClicked(it)
            dismiss()
        }
    }

    private var _binding: FragmentSearchableBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var data: List<SearchableBottomSheetData> = mutableListOf()

    private val disposables = CompositeDisposable()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also { dialog ->
            dialog.setOnShowListener {
                if (isTablet()) {
                    dialog.findViewById<View>(R.id.design_bottom_sheet)?.also { bottomSheet ->
                        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchableBottomSheetBinding.inflate(inflater, container, false)
        binding.searchableList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = controller.adapter
        }

        binding.searchableBottomSheetClose.setOnClickListener {
            dismiss()
        }

        binding.search.queryTextChanges()
            .debounce(200, TimeUnit.MILLISECONDS)
            .subscribe { query ->
                val searchResults = data.filter {
                    it.name.toLowerCase().contains(query.toString().toLowerCase())
                }
                controller.setData(searchResults)
            }.addTo(disposables)

        if (data.any { it is SearchableConditionData }) {
            binding.searchableBottomSheetTitle.text = getString(R.string.condition)
            binding.search.queryHint = getString(R.string.search_condition)
        } else if (data.any { it is SearchableLocationData }) {
            binding.searchableBottomSheetTitle.text = getString(R.string.location)
            binding.search.queryHint = getString(R.string.search_location)
        }

        return binding.root
    }

    override fun onDestroy() {
        disposables.clear()
        _binding = null
        super.onDestroy()
    }

    fun setData(data: List<SearchableBottomSheetData>) {
        this.data = data.sortedBy { it.name }
        controller.setData(data)
    }
}

sealed class SearchableBottomSheetData(val name: String) {
    data class SearchableConditionData(val condition: Condition) :
        SearchableBottomSheetData(condition.name)

    data class SearchableLocationData(val location: RackLocation) :
        SearchableBottomSheetData(location.name)

    data class SearchableReasonData(val reason: Reason) :
        SearchableBottomSheetData(reason.uiName)
}