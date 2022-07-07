package dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.scgts.sctrace.root.components.databinding.DialogOfflineSyncBinding

class OfflineSyncDialog(
    private val okayClickListener: (() -> Unit)
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DialogOfflineSyncBinding.inflate(layoutInflater).let {
            it.okayButton.setOnClickListener {
                okayClickListener.invoke()
                dismiss()
            }
            AlertDialog.Builder(requireContext())
                .setView(it.root)
                .create()
        }
    }
}