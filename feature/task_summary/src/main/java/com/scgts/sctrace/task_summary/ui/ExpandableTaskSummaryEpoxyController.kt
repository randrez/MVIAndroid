package com.scgts.sctrace.task_summary.ui

import com.airbnb.epoxy.TypedEpoxyController
import com.scgts.sctrace.base.model.Length
import com.scgts.sctrace.base.model.OrderType
import com.scgts.sctrace.base.model.OrderType.*
import com.scgts.sctrace.base.model.TaskStatus.*
import com.scgts.sctrace.base.model.TaskType
import com.scgts.sctrace.base.model.TaskType.*
import com.scgts.sctrace.task_summary.R
import com.scgts.sctrace.task_summary.ui.ExpandableTaskSummaryEpoxyController.ExpandabletaskSummaryRow.*
import com.scgts.sctrace.task_summary.ui.TaskSummaryMvi.ViewState
import com.scgts.sctrace.ui.components.expandButtonRow

class ExpandableTaskSummaryEpoxyController(
    val toggleExpand: () -> Unit,
    val seeDetails: () -> Unit,
    private val isTablet: Boolean,
) :
    TypedEpoxyController<ViewState>() {

    /**
     * Expanding task summary contents are determined by a combination of a task's [OrderType] and
     * [TaskType]. The only constants are that each will begin with the task status and have the
     * expand/collapse button at the bottom except for [AD_HOC_REJECT_SCAN].
     *
     * Supported [OrderType] - [TaskType] Combinations:
     *
     *              [INBOUND] - [INBOUND_FROM_MILL]
     *
     *             [OUTBOUND] - [BUILD_ORDER]
     *             [OUTBOUND] - [DISPATCH]
     *             [OUTBOUND] - [INBOUND_TO_WELL]
     *
     *          [CONSUMPTION] - [CONSUME]
     *
     *      [RETURN_TRANSFER] - [DISPATCH]
     *      [RETURN_TRANSFER] - [INBOUND_TO_WELL]
     *      [RETURN_TRANSFER] - [INBOUND_TALLY]
     *
     *      [QUICK_INBOUND_FROM_MILL] - [INBOUND_FROM_MILL]
     *
     * NOTE: AD_HOC tasks are created locally from the quick actions menu and will never come from
     * server.
     *
     *      [RETURN_TRANSFER] - [AD_HOC_INBOUND_TO_WELL]
     *      [RETURN_TRANSFER] - [AD_HOC_INBOUND_TALLY]
     *      [RETURN_TRANSFER] - [AD_HOC_DISPATCH_RETURN]
     *      [RETURN_TRANSFER] - [AD_HOC_DISPATCH_TRANSFER]
     *      [QUICK_INBOUND_FROM_MILL] - [AD_HOC_INBOUND_FROM_MILL]
     *
     * Note that readability and ease of modification was consciously chosen over optimization here!
     */
    override fun buildModels(viewState: ViewState) {


        viewState.task?.let { task ->
            val rowData = viewState.toExpandableTaskSummaryRowModelData()

            // All combinations will have status
            expandableTaskSummaryStatusRow {
                id("Status")
                field("Status")
                value(task.status.uiName)
                val tint = when (task.status) {
                    NOT_STARTED -> R.color.n_060
                    IN_PROGRESS -> R.color.t_100
                    PENDING -> R.color.yellow
                    IN_REVIEW -> R.color.p_200
                    COMPLETED -> R.color.green
                    else -> R.color.red
                }
                statusColor(tint)
            }

            when (task.orderType) {
                INBOUND -> {
                    when (task.type) {
                        INBOUND_FROM_MILL, AD_HOC_INBOUND_FROM_MILL -> {
                            // collapsed state
                            expandableTaskSummaryRow {
                                id(TOTAL_TALLY.ordinal)
                                type(TOTAL_TALLY)
                                rowData(rowData)
                            }
                            expandableTaskSummaryRow {
                                id(EXPECTED_TALLY.ordinal)
                                type(EXPECTED_TALLY)
                                rowData(rowData)
                            }
                            // expanded state
                            if (viewState.summaryExpanded) {
                                expandableTaskSummaryRow {
                                    id(SESSION_TALLY.ordinal)
                                    type(SESSION_TALLY)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryClickableValueRow {
                                    id("See details")
                                    field("Order")
                                    value("See details")
                                    clickListener(seeDetails)
                                }
                            }
                        }
                    }
                }
                OUTBOUND -> {
                    when (task.type) {
                        BUILD_ORDER, DISPATCH -> {
                            // collapsed state
                            expandableTaskSummaryRow {
                                id(TOTAL_TALLY.ordinal)
                                type(TOTAL_TALLY)
                                rowData(rowData)
                            }
                            expandableTaskSummaryRow {
                                id(EXPECTED_TALLY.ordinal)
                                type(EXPECTED_TALLY)
                                rowData(rowData)
                            }
                            // expanded state
                            if (viewState.summaryExpanded) {
                                expandableTaskSummaryRow {
                                    id(SESSION_TALLY.ordinal)
                                    type(SESSION_TALLY)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(TO_LOCATION.ordinal)
                                    type(TO_LOCATION)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(DELIVERY_DATE.ordinal)
                                    type(DELIVERY_DATE)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(TOTAL_RUN_LENGTH.ordinal)
                                    type(TOTAL_RUN_LENGTH)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryClickableValueRow {
                                    id("See details")
                                    field("Order")
                                    value("See details")
                                    clickListener(seeDetails)
                                }
                            }
                        }
                        INBOUND_TO_WELL -> {
                            // collapsed state
                            expandableTaskSummaryRow {
                                id(TOTAL_TALLY.ordinal)
                                type(TOTAL_TALLY)
                                rowData(rowData)
                            }
                            expandableTaskSummaryRow {
                                id(EXPECTED_TALLY.ordinal)
                                type(EXPECTED_TALLY)
                                rowData(rowData)
                            }
                            // expanded state
                            if (viewState.summaryExpanded) {
                                expandableTaskSummaryRow {
                                    id(TOTAL_RUN_LENGTH.ordinal)
                                    type(TOTAL_RUN_LENGTH)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(SESSION_TALLY.ordinal)
                                    type(SESSION_TALLY)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(CUSTOMER.ordinal)
                                    type(CUSTOMER)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryClickableValueRow {
                                    id("See details")
                                    field("Order")
                                    value("See details")
                                    clickListener(seeDetails)
                                }
                            }
                        }
                    }
                }
                CONSUMPTION -> {
                    when (task.type) {
                        CONSUME -> {
                            // collapsed state
                            expandableTaskSummaryRow {
                                id(TOTAL_CONSUMED.ordinal)
                                type(TOTAL_CONSUMED)
                                rowData(rowData)
                            }
                            expandableTaskSummaryRow {
                                id(SESSION_CONSUMED.ordinal)
                                type(SESSION_CONSUMED)
                                rowData(rowData)
                            }
                            // expanded state
                            if (viewState.summaryExpanded) {
                                expandableTaskSummaryRow {
                                    id(TOTAL_RUN_LENGTH.ordinal)
                                    type(TOTAL_RUN_LENGTH)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(TOTAL_REJECTED.ordinal)
                                    type(TOTAL_REJECTED)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(WELL_SECTION.ordinal)
                                    type(WELL_SECTION)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryClickableValueRow {
                                    id("See details")
                                    field("Order")
                                    value("See details")
                                    clickListener(seeDetails)
                                }
                            }
                        }
                    }
                }
                RETURN_TRANSFER -> {
                    when (task.type) {
                        DISPATCH -> {
                            // collapsed state
                            expandableTaskSummaryRow {
                                id(TOTAL_TALLY.ordinal)
                                type(TOTAL_TALLY)
                                rowData(rowData)
                            }
                            // expanded state
                            if (viewState.summaryExpanded) {
                                expandableTaskSummaryRow {
                                    id(FROM_LOCATION.ordinal)
                                    type(FROM_LOCATION)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(TO_LOCATION.ordinal)
                                    type(TO_LOCATION)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(DISPATCH_DATE.ordinal)
                                    type(DISPATCH_DATE)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(SENDER.ordinal)
                                    type(SENDER)
                                    rowData(rowData)
                                }
                            }
                        }
                        INBOUND_TO_WELL -> {
                            // collapsed state
                            expandableTaskSummaryRow {
                                id(TOTAL_TALLY.ordinal)
                                type(TOTAL_TALLY)
                                rowData(rowData)
                            }
                            expandableTaskSummaryRow {
                                id(EXPECTED_TALLY.ordinal)
                                type(EXPECTED_TALLY)
                                rowData(rowData)
                            }
                            // expanded state
                            if (viewState.summaryExpanded) {
                                expandableTaskSummaryRow {
                                    id(SESSION_TALLY.ordinal)
                                    type(SESSION_TALLY)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(TOTAL_RUN_LENGTH.ordinal)
                                    type(TOTAL_RUN_LENGTH)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(CUSTOMER.ordinal)
                                    type(CUSTOMER)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryClickableValueRow {
                                    id("See details")
                                    field("Order")
                                    value("See details")
                                    clickListener(seeDetails)
                                }
                            }
                        }
                        INBOUND_FROM_WELL_SITE -> {
                            // collapsed state
                            expandableTaskSummaryRow {
                                id(TOTAL_TALLY.ordinal)
                                type(TOTAL_TALLY)
                                rowData(rowData)
                            }
                            // expanded state
                            if (viewState.summaryExpanded) {
                                expandableTaskSummaryRow {
                                    id(SESSION_TALLY.ordinal)
                                    type(SESSION_TALLY)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(TOTAL_RUN_LENGTH.ordinal)
                                    type(TOTAL_RUN_LENGTH)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryClickableValueRow {
                                    id("See details")
                                    field("Order")
                                    value("See details")
                                    clickListener(seeDetails)
                                }
                            }
                        }
                        RACK_TRANSFER -> {
                            // collapsed state
                            expandableTaskSummaryRow {
                                id(TOTAL_TALLY.ordinal)
                                type(TOTAL_TALLY)
                                rowData(rowData)
                            }

                            expandableTaskSummaryRow {
                                id(SESSION_TALLY.ordinal)
                                type(SESSION_TALLY)
                                rowData(rowData)
                            }
                            // expanded state
                            if (viewState.summaryExpanded) {
                                expandableTaskSummaryRow {
                                    id(TOTAL_RUN_LENGTH.ordinal)
                                    type(TOTAL_RUN_LENGTH)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryClickableValueRow {
                                    id("See details")
                                    field("Order")
                                    value("See details")
                                    clickListener(seeDetails)
                                }
                            }
                        }
                        AD_HOC_INBOUND_FROM_WELL_SITE -> {
                            // collapsed state
                            expandableTaskSummaryRow {
                                id(TOTAL_TALLY.ordinal)
                                type(TOTAL_TALLY)
                                rowData(rowData)
                            }
                            // expanded state
                            if (viewState.summaryExpanded) {
                                expandableTaskSummaryRow {
                                    id(TOTAL_RUN_LENGTH.ordinal)
                                    type(TOTAL_RUN_LENGTH)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(YARD_NAME.ordinal)
                                    type(YARD_NAME)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(ARRIVAL_DATE.ordinal)
                                    type(ARRIVAL_DATE)
                                    rowData(rowData)
                                }
                            }
                        }
                        AD_HOC_INBOUND_TO_WELL -> {
                            // collapsed state
                            expandableTaskSummaryRow {
                                id(TOTAL_TALLY.ordinal)
                                type(TOTAL_TALLY)
                                rowData(rowData)
                            }
                            // expanded state
                            if (viewState.summaryExpanded) {
                                expandableTaskSummaryRow {
                                    id(TOTAL_RUN_LENGTH.ordinal)
                                    type(TOTAL_RUN_LENGTH)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(ARRIVAL_DATE.ordinal)
                                    type(ARRIVAL_DATE)
                                    rowData(rowData)
                                }
                            }
                        }
                        AD_HOC_DISPATCH_TO_YARD, AD_HOC_DISPATCH_TO_WELL -> {
                            // collapsed state
                            expandableTaskSummaryRow {
                                id(TOTAL_TALLY.ordinal)
                                type(TOTAL_TALLY)
                                rowData(rowData)
                            }
                            // expanded state
                            if (viewState.summaryExpanded) {
                                expandableTaskSummaryRow {
                                    id(FROM_LOCATION.ordinal)
                                    type(FROM_LOCATION)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(TO_LOCATION.ordinal)
                                    type(TO_LOCATION)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(DISPATCH_DATE.ordinal)
                                    type(DISPATCH_DATE)
                                    rowData(rowData)
                                }
                                expandableTaskSummaryRow {
                                    id(SENDER.ordinal)
                                    type(SENDER)
                                    rowData(rowData)
                                }
                            }
                        }
                    }
                }
            }

            // Special case - quick reject action will be created ad hoc locally and does not have
            // the expand/collapse button at the bottom
            if (task.type == AD_HOC_REJECT_SCAN) {
                expandableTaskSummaryRow {
                    id(TOTAL_REJECTED.ordinal)
                    type(TOTAL_REJECTED)
                    rowData(rowData)
                }
                expandableTaskSummaryRow {
                    id(TOTAL_RUN_LENGTH.ordinal)
                    type(TOTAL_RUN_LENGTH)
                    rowData(rowData)
                }
            } else {
                if (isTablet) return
                expandButtonRow {
                    id("expandbuttonrow")
                    imageSrc(if (viewState.summaryExpanded) R.drawable.ic_scgts_collapse else R.drawable.ic_scgts_expand)
                    clickListener { toggleExpand() }
                }
            }
        }
    }

    private fun ViewState.toExpandableTaskSummaryRowModelData(): ExpandableTaskSummaryRowModelData =
        ExpandableTaskSummaryRowModelData(
            task = task!!,
            unitType = unitType,
            assets = assets.filter { it.checked != false },
            totalTally = totalTalliesAndJoints.total,
            totalConsumed = totalTalliesAndJoints.totalConsumed,
            totalRejected = totalTalliesAndJoints.totalRejected,
            totalJoints = totalTalliesAndJoints.totalJoints,
            consumedJoints = totalTalliesAndJoints.consumedJoints,
            rejectedJoints = totalTalliesAndJoints.rejectedJoints,
            sessionTally = sessionTalliesAndJoints.sessionTotal,
            sessionJoints = sessionTalliesAndJoints.sessionTotalJoints,
            sessionConsumed = sessionTalliesAndJoints.sessionConsumed,
            sessionConsumedJoints = sessionTalliesAndJoints.sessionConsumedJoints,
            totalConsumedRunningLength = Length(totalTalliesAndJoints.consumedRunningLength, unitType),
            totalRunningLength = Length(totalTalliesAndJoints.runningLength, unitType),
            totalMakeUpLoss = totalTalliesAndJoints.totalMakeUpLoss,
            sessionMakeUpLoss = sessionTalliesAndJoints.sessionMakeUpLoss
        )

    enum class ExpandabletaskSummaryRow {
        TOTAL_TALLY,
        EXPECTED_TALLY,
        SESSION_TALLY,
        ARRIVAL_DATE,
        TOTAL_CONSUMED,
        SESSION_CONSUMED,
        FROM_LOCATION,
        TO_LOCATION,
        DELIVERY_DATE,
        TOTAL_RUN_LENGTH,
        WELL_SECTION,
        CUSTOMER,
        TOTAL_REJECTED,
        DISPATCH_DATE,
        SENDER,
        YARD_NAME
    }
}
