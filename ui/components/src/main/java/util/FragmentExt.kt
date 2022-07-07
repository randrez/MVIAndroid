package util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.scgts.sctrace.root.components.R

fun Fragment.hideKeyboard(view: View, clearFocus: Boolean = false) {
    (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.also {
        it.hideSoftInputFromWindow(view.windowToken, 0)
        if (clearFocus) {
            view.clearFocus()
        }
    }
}

fun EditText.showKeyboard(): Boolean {
    return (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(this, 0)
}

fun EditText.hideKeyboard(): Boolean {
    return (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, 0)
}

fun Fragment.dpToPixels(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

fun Fragment.isTablet() = resources.getBoolean(R.bool.isTablet)

