package com.scgts.sctrace.tasks

import io.reactivex.rxjava3.core.Completable

interface ProjectsRepository {
    fun syncRemote(): Completable
    fun syncAssets(): Completable
}
