package com.scgts.sctrace.assets.length

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import dialogs.BaseDialogFragment
import com.scgts.sctrace.assets.confirmation.databinding.FragmentEditAssetLengthBinding
import com.scgts.sctrace.assets.length.EditAssetLengthMvi.Intent
import com.scgts.sctrace.assets.length.EditAssetLengthMvi.Intent.*
import com.scgts.sctrace.assets.length.EditAssetLengthMvi.ViewState
import org.koin.android.viewmodel.ext.android.viewModel
import util.hideKeyboard

class EditAssetLengthFragment : BaseDialogFragment<Intent, ViewState, EditAssetLengthViewModel>() {
    override val viewModel: EditAssetLengthViewModel by viewModel()
    private lateinit var binding: FragmentEditAssetLengthBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditAssetLengthBinding.inflate(inflater, container, false)
        with(binding) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lengthEditText.setOnFocusChangeListener { _, hasFocus ->
                    intents.onNext(IsEditing(hasFocus))
                }
            } else lengthEditText.setOnClickListener { intents.onNext(IsEditing(true)) }

            cancelButton.setOnClickListener { intents.onNext(IsEditing(false)) }
            saveButton.setOnClickListener {
                val regex = Regex("[A-Za-z ]")
                val lengthString = lengthEditText.text.toString().replace(regex, "")
                if (lengthString.isNotEmpty()) intents.onNext(SaveLength(lengthString.toDouble()))
            }
            doneButton.setOnClickListener { intents.onNext(Done) }
        }
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        return binding.root
    }

    override fun render(viewState: ViewState) {
        with(binding) {
            lengthEditText.setText(viewState.length.getFormattedLengthString())
            saveButton.isVisible = viewState.isEditing
            cancelButton.isVisible = viewState.isEditing
            doneButton.isVisible = !viewState.isEditing
            if (!viewState.isEditing) hideKeyboard(lengthEditText, clearFocus = true)
        }
    }
}