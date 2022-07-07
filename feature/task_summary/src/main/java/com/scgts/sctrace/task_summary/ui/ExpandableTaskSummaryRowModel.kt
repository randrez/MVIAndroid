package com.scgts.sctrace.task_summary.ui

import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.base.util.formatTally
import com.scgts.sctrace.base.util.toFormattedDate
import com.scgts.sctrace.root.components.R
import com.scgts.sctrace.root.components.databinding.RowExpandableTaskSummaryBinding
import com.scgts.sctrace.task_summary.ui.ExpandableTaskSummaryEpoxyController.ExpandabletaskSummaryRow
import com.scgts.sctrace.task_summary.ui.ExpandableTaskSummaryEpoxyController.ExpandabletaskSummaryRow.*

@EpoxyModelClass
abstract class ExpandableTaskSummaryRowModel : EpoxyModel<ConstraintLayout>() {
    @EpoxyAttribute
    lateinit var rowData: ExpandableTaskSummaryRowModelData

    @EpoxyAttribute
    lateinit var type: ExpandabletaskSummaryRow

    override fun getDefaultLayout() = R.layout.row_expandable_task_summary

    override fun bind(view: ConstraintLayout) {
        super.bind(view)

        val binding = RowExpandableTaskSummaryBinding.bind(view)
        with(binding) {
            when (type) {
                TOTAL_TALLY -> {
                    summaryField.text = "Total tally"
                    summaryValue.text =
                        formatTally(
                            rowData.totalTally,
                            rowData.totalJoints,
                            rowData.unitType
                        )
                }
                EXPECTED_TALLY -> {
                    summaryField.text = "Expected tally"
                    summaryValue.text = formatTally(
                        rowData.task.totalExpectedLength,
                        rowData.task.totalNumJoints,
                        rowData.unitType
                    )
                }
                SESSION_TALLY -> {
                    summaryField.text = "Session tally"
                    summaryValue.text = formatTally(
                        rowData.sessionTally,
                        rowData.sessionJoints,
                        rowData.unitType
                    )
                }
                ARRIVAL_DATE -> {
                    summaryField.text = "Arrival date"
                    summaryValue.text = rowData.task.arrivalDate?.toFormattedDate()
                }
                TOTAL_CONSUMED -> {
                    summaryField.text = "Total consumed"
                    summaryValue.text = formatTally(
                        rowData.totalConsumed,
                        rowData.consumedJoints,
                        rowData.unitType
                    )
                }
                SESSION_CONSUMED -> {
                    summaryField.text = "Session consumed"
                    summaryValue.text = formatTally(
                        rowData.sessionConsumed,
                        rowData.sessionConsumedJoints,
                        rowData.unitType
                    )
                }
                FROM_LOCATION -> {
                    summaryField.text = "From"
                    summaryValue.text = rowData.task.fromLocationName
                }
                TO_LOCATION -> {
                    summaryField.text = "To"
                    summaryValue.text = rowData.task.toLocationName
                }
                DELIVERY_DATE -> {
                    summaryField.text = "Delivery date"
                    summaryValue.text = rowData.task.deliveryDate.toFormattedDate()
                }
                TOTAL_RUN_LENGTH -> {
                    summaryField.text = "Total run. length"
                    val totalRunningLength = if (rowData.task.type == TaskType.CONSUME) {
                        rowData.totalConsumedRunningLength.getFormattedLengthString()
                    } else {
                        rowData.totalRunningLength.getFormattedLengthString()
                    }
                    summaryValue.text = totalRunningLength
                }
                WELL_SECTION -> {
                    summaryField.text = "Well section"
                    summaryValue.text = rowData.task.wellSection
                }
                CUSTOMER -> {
                    summaryField.text = "Customer"
                    summaryValue.text = rowData.task.organizationName
                }
                TOTAL_REJECTED -> {
                    summaryField.text = "Total rejected"
                    summaryValue.text = formatTally(
                        rowData.totalRejected,
                        rowData.rejectedJoints,
                        rowData.unitType
                    )
                }
                DISPATCH_DATE -> {
                    summaryField.text = "Dispatch date"
                    summaryValue.text = rowData.task.dispatchDate?.toFormattedDate()
                }
                SENDER -> {
                    summaryField.text = "Sender"
                    summaryValue.text = rowData.task.organizationName
                }
                YARD_NAME -> {
                    summaryField.text = "Yard name"
                    summaryValue.text = rowData.task.toLocationName
                }
            }

        }
    }
}

data class ExpandableTaskSummaryRowModelData(
    val task: Task,
    val unitType: UnitType,
    val assets: List<AssetCardUiModel>,
    val totalTally: Double,
    val totalConsumed: Double,
    val totalRejected: Double,
    val totalJoints: Int,
    val consumedJoints: Int,
    val rejectedJoints: Int,
    val sessionTally: Double,
    val sessionJoints: Int,
    val sessionConsumed: Double,
    val sessionConsumedJoints: Int,
    val totalConsumedRunningLength: Length,
    val totalRunningLength: Length,
    val totalMakeUpLoss: Double,
    val sessionMakeUpLoss: Double
)