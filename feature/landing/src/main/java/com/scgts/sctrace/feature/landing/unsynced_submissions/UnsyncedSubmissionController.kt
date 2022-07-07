package com.scgts.sctrace.feature.landing.unsynced_submissions

import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.scgts.sctrace.base.model.TaskType.AD_HOC_REJECT_SCAN
import com.scgts.sctrace.base.model.TaskType.AD_HOC_QUICK_SCAN
import com.scgts.sctrace.base.model.UnsyncedSubmission
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.root.components.databinding.ItemUnsyncedSubmissionBinding
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class UnsyncedSubmissionController : TypedEpoxyController<List<UnsyncedSubmission>>() {
    override fun buildModels(data: List<UnsyncedSubmission>) {
        data.forEachIndexed { index, item ->
            if (item.taskOrderType == AD_HOC_QUICK_SCAN.id ||
                item.taskOrderType == AD_HOC_REJECT_SCAN.id
            ) {
                unsyncedAdhocScan {
                    id(index)
                    quickReject(item.taskOrderType == AD_HOC_REJECT_SCAN.id)
                    scanNum(item.assetCount)
                }
            } else {
                unsyncedSubmission {
                    id(item.taskOrderType + item.taskTypeString + item.taskDescription)
                    taskOrderType(item.taskOrderType)
                    taskTypeString(item.taskTypeString)
                    taskDescription(item.taskDescription)
                    updatedAtTime(item.capturedAt)
                    tallyText(item.tallyText)
                    timeLabel(item.timeLabel)
                }
            }
        }
    }
}

@EpoxyModelClass
abstract class UnsyncedSubmissionModel : EpoxyModel<CardView>() {
    @EpoxyAttribute
    var updatedAtTime: ZonedDateTime? = null

    @EpoxyAttribute
    lateinit var taskOrderType: String

    @EpoxyAttribute
    lateinit var taskTypeString: String

    @EpoxyAttribute
    lateinit var taskDescription: String

    @EpoxyAttribute
    var tallyText: String? = null

    @EpoxyAttribute
    lateinit var timeLabel: String

    override fun getDefaultLayout(): Int = R.layout.item_unsynced_submission

    override fun bind(view: CardView) {
        super.bind(view)
        with(ItemUnsyncedSubmissionBinding.bind(view)) {
            taskType.text = if (taskOrderType.isEmpty()) {
                taskTypeString
            } else "$taskOrderType / $taskTypeString"
            taskJobId.text = taskDescription
            scanTimeLabel.text = timeLabel
            if (tallyText.isNullOrEmpty()) {
                taskExpectedTally.visibility = View.INVISIBLE
                taskExpectedTallyValue.visibility = View.INVISIBLE
            } else {
                taskExpectedTally.visibility = View.VISIBLE
                taskExpectedTallyValue.visibility = View.VISIBLE
                taskExpectedTallyValue.text = tallyText
            }


            scanTime.text = updatedAtTime?.withZoneSameInstant(ZoneOffset.systemDefault())
                ?.format(DateTimeFormatter.ofPattern("MMM dd 'at' hh:mma"))
                ?.replace("AM", "am")
                ?.replace("PM", "pm")
        }
    }
}

@EpoxyModelClass
abstract class UnsyncedAdhocScan : EpoxyModel<CardView>() {
    @EpoxyAttribute
    var scanNum: Int = 0

    @EpoxyAttribute
    var quickReject: Boolean = false

    override fun getDefaultLayout(): Int = R.layout.item_unsynced_submission

    override fun bind(view: CardView) {
        super.bind(view)
        with(ItemUnsyncedSubmissionBinding.bind(view)) {
            taskType.text = view.context.getString(
                if (quickReject) R.string.reject_scan else R.string.ad_hoc_scan
            )
            taskJobId.text = view.context.getString(R.string.assets_updated_num, scanNum)

            taskExpectedTallyValue.isVisible = false
            taskExpectedTally.isVisible = false
            scanTime.isVisible = false
            scanTimeLabel.isVisible = false
        }
    }
}