package dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.scgts.sctrace.root.components.databinding.DialogAssetFromWrongProjectBinding

class AssetFromWrongProjectDialog(
    private val message: String,
    private val dismissClickListener: (() -> Unit),
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DialogAssetFromWrongProjectBinding.inflate(layoutInflater).let {
            it.dialogDescription.text = message
            it.dismissButton.setOnClickListener {
                dismissClickListener.invoke()
                dismiss()
            }
            isCancelable = false
            AlertDialog.Builder(requireContext())
                .setView(it.root)
                .create()
        }
    }
}