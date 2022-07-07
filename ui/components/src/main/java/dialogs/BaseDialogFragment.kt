package dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.scgts.framework.mvi.MviIntent
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.MviViewState
import com.scgts.sctrace.root.components.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.PublishSubject
import util.isTablet
import util.sendErrorToDtrace

abstract class BaseDialogFragment<I : MviIntent, VS : MviViewState, V : MviViewModel<I, VS>> :
    BottomSheetDialogFragment() {
    protected val intents: PublishSubject<I> = PublishSubject.create()
    protected abstract val viewModel: V
    private val _viewState = MutableLiveData<VS>()
    protected val viewState: LiveData<VS> get() = _viewState
    protected val disposables = CompositeDisposable()
    private var normalPeekHeight: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.bind(intents.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::render) { error: Throwable -> error.sendErrorToDtrace(this.javaClass.name) }
            .addTo(disposables)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (isTablet()) {
            Dialog(requireContext(), theme)
        } else BottomSheetDialog(requireContext(), theme)
    }

    open fun render(viewState: VS) {
        _viewState.postValue(viewState)
    }

    fun changeViewHeight(isExpanded: Boolean) {
        if (isTablet()) return
        getBehaviorBottomSheet(isExpanded)?.peekHeight =
            if (isExpanded) resources.displayMetrics.heightPixels else normalPeekHeight ?: 0
        view?.requestLayout()
    }

    private fun getBehaviorBottomSheet(isExpanded: Boolean): BottomSheetBehavior<View>? {
        return dialog?.let {
            val bottomSheet = it.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height =
                if (isExpanded) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
            val behavior = BottomSheetBehavior.from(bottomSheet)
            if (normalPeekHeight == null) {
                normalPeekHeight = behavior.peekHeight
            }
            behavior
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}