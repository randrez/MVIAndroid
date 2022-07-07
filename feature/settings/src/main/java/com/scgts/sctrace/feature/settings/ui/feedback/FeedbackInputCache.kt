package com.scgts.sctrace.feature.settings.ui.feedback

import com.scgts.sctrace.base.model.FeedbackOption
import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache

class FeedbackInputCache : InMemoryObjectCache<FeedbackInput>(FeedbackInput())

data class FeedbackInput(
    var feedbackType: FeedbackOption? = null,
    var severity: FeedbackOption? = null,
    var details: String = "",
)