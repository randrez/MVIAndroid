package util

import com.dynatrace.android.agent.Dynatrace
import timber.log.Timber

fun Throwable.sendErrorToDtrace(className:String){
    Timber.e("Class:$className   Exception:${this.stackTraceToString()}")
    Dynatrace.modifyUserAction { userAction ->
        userAction.actionName = "Error"
        userAction.reportValue(className, this.stackTraceToString())
    }
}