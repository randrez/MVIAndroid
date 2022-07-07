package dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.scgts.sctrace.root.components.databinding.DialogWarningBinding

class WarningDialogFragment(
    @StringRes val warningChallengeRes: Int,
    @StringRes val warningExplanationRes: Int,
    val showChecbox: Boolean = true,
    val positiveClickListener: (Boolean) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogWarningBinding.inflate(layoutInflater)

        binding.checkbox.isVisible = showChecbox
        binding.negativeButton.setOnClickListener { dismiss() }
        binding.warningChallenge.text = getString(warningChallengeRes)
        binding.warningExplanation.text = getString(warningExplanationRes)
        binding.positiveButton.setOnClickListener {
            positiveClickListener(binding.checkbox.isChecked)
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }
}