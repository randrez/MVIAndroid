package com.scgts.sctrace.task_summary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.scgts.sctrace.task_summary.databinding.FragmentDiscardConfirmationBinding

class DiscardConfirmationFragment(val deleteClicked: () -> Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDiscardConfirmationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscardConfirmationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with (binding) {
            trashCanIcon.setOnClickListener {
                deleteClicked()
                dismiss()
            }
            trashText.setOnClickListener {
                deleteClicked()
                dismiss()
            }

            cancelIcon.setOnClickListener { dismiss() }
            cancelText.setOnClickListener { dismiss() }
        }
    }
}