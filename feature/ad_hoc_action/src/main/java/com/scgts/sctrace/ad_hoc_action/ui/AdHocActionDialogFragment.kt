package com.scgts.sctrace.ad_hoc_action.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.scgts.sctrace.ad_hoc_action.R
import com.scgts.sctrace.ad_hoc_action.databinding.FragmentDialogAdHocActionBinding

class AdHocActionDialogFragment : DialogFragment() {
    private val args: AdHocActionDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentDialogAdHocActionBinding.inflate(layoutInflater).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager
            .beginTransaction()
            .add(
                R.id.fragment_container,
                AdHocActionFragment().apply {
                    this.arguments = AdHocActionFragmentArgs(args.action).toBundle()
                }
            )
            .commit()
    }
}