package dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.scgts.sctrace.root.components.databinding.DialogConsumptionDuplicateBinding

class ConsumptionDuplicateDialog(
    private val message: String,
    private val okayClickListener: (() -> Unit),
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DialogConsumptionDuplicateBinding.inflate(layoutInflater).let {
            it.duplicateTitle.text = message
            it.okayButton.setOnClickListener {
                okayClickListener.invoke()
                dismiss()
            }
            isCancelable = false
            AlertDialog.Builder(requireContext())
                .setView(it.root)
                .create()
        }
    }
}