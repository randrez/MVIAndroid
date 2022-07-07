package com.scgts.sctrace.queue

import android.os.Build
import com.dynatrace.android.agent.DTXAction
import com.dynatrace.android.agent.Dynatrace
import com.google.gson.Gson
import com.scgts.sctrace.base.model.DeleteTagPayload
import com.scgts.sctrace.base.model.MiscellaneousQueue
import com.scgts.sctrace.base.model.PayloadType
import com.scgts.sctrace.base.model.UserFeedbackPayload
import com.scgts.sctrace.database.dao.MiscellaneousQueueDao
import com.scgts.sctrace.network.ApolloService
import com.scgts.sctrace.queue.BuildConfig.VERSION_NAME
import com.scgts.sctrace.tasks.TasksRepository
import com.scgts.sctrace.tasks.mappers.toEntity
import com.scgts.sctrace.tasks.mappers.uiModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.flatMapIterable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import util.TopToastManager
import util.sendErrorToDtrace

class QueueRepositoryImpl(
    private val miscellaneousQueueDao: MiscellaneousQueueDao,
    private val apolloService: ApolloService,
    private val topToastManager: TopToastManager,
    private val taskRepository: TasksRepository,
) : QueueRepository {

    override fun hasMiscellaneousQueue(): Observable<Boolean> {
        return miscellaneousQueueDao.getQueueCount().distinctUntilChanged().map { it > 0 }
            .subscribeOn(Schedulers.io())
    }

    override fun addDeleteTagToMiscQueue(assetId: String, tag: String): Completable {
        return miscellaneousQueueDao.insert(
            MiscellaneousQueue(
                payloadType = PayloadType.DELETE_TAG,
                payload = DeleteTagPayload(assetId, tag)
            ).toEntity()
        ).subscribeOn(Schedulers.io())
    }

    override fun submitMiscellaneousQueue(): Completable {
        return miscellaneousQueueDao.getQueueSingle().toObservable().flatMapIterable()
            .flatMapCompletable { queueEntity ->
                val queueItem = queueEntity.uiModel()
                when (queueItem.payloadType) {
                    PayloadType.DELETE_TAG -> {
                        val payload: DeleteTagPayload = queueItem.payload as DeleteTagPayload
                        apolloService.pushDeleteTag(payload.assetId, payload.tag)
                            .andThen(miscellaneousQueueDao.delete(queueEntity))
                    }
                    PayloadType.USER_FEEDBACK -> {
                        val payload: UserFeedbackPayload = queueItem.payload as UserFeedbackPayload
                        val tags = mutableListOf(
                            payload.feedbackType,
                            "Mobile-Android",
                            "Version: $VERSION_NAME",
                            "${Build.MANUFACTURER} ${Build.MODEL}"
                        )

                        val severity = payload.feedbackSeverity
                        if (!severity.isNullOrEmpty()) {
                            tags.add(severity)
                        }

                        taskRepository.getDetailFeedbackDtrace().flatMapCompletable { detail ->
                            Dynatrace.identifyUser(detail.userEmail)
                            val action = Dynatrace.enterAction("Feedback ${detail.userEmail}")
                            action.reportValue(detail.getTitleDetailFeedbackDtrace(),
                                detail.getInformationDetailFeedbackDtrace())
                            apolloService.pushFeedback(
                                payload.feedback,
                                detail.userEmail,
                                tags
                            )
                                .andThen(topToastManager.showToast("Your feedback has been submitted. Thank you!"))
                                .andThen(miscellaneousQueueDao.delete(queueEntity))
                                .andThen(closeAction(action))
                        }
                    }
                    else -> Completable.complete()
                }
            }.doOnError { error: Throwable -> sendError(error) }.subscribeOn(Schedulers.io())
    }

    private fun closeAction(action: DTXAction?): Completable {
        action?.leaveAction()
        return Completable.complete()
    }

    override fun addFeedbackToMiscQueue(
        feedbackType: String,
        feedbackSeverity: String?,
        feedback: String,
    ): Completable {
        return miscellaneousQueueDao.insert(
            MiscellaneousQueue(
                payloadType = PayloadType.USER_FEEDBACK,
                payload = UserFeedbackPayload(
                    feedbackType = feedbackType,
                    feedbackSeverity = feedbackSeverity,
                    feedback = feedback,
                    timeStamp = ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond()
                )
            ).toEntity()
        ).subscribeOn(Schedulers.io())
    }

    private fun sendError(exception: Throwable) {
        exception.sendErrorToDtrace(this.javaClass.name)
    }

    override fun getUnSubmittedFeedback(): Single<List<UserFeedbackPayload>> {
        return miscellaneousQueueDao.getQueueSingle().map {
            val list = mutableListOf<UserFeedbackPayload>()
            for (entity in it) {
                if (entity.payloadType == PayloadType.USER_FEEDBACK.serverName) {
                    val payload = Gson().fromJson(entity.payload, UserFeedbackPayload::class.java)
                    list.add(payload)
                }
            }
            list
        }
    }
}