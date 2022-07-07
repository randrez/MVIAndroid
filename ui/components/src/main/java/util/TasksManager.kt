package util

import com.scgts.sctrace.base.model.SortCategory
import com.scgts.sctrace.base.model.TasksFilterAndSort
import com.scgts.sctrace.in_memory_cache.InMemoryObjectCache
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface TasksManager {
    fun getCurrentFilterAndSort(): Single<TasksFilterAndSort>
    fun getFilterAndSortObservable(): Observable<TasksFilterAndSort>
    fun getFilterCountObservable(): Observable<Int>
    fun updateFilterAndSort(
        newCategory: SortCategory? = null,
        projectIds: List<String>? = null,
        taskTypes: List<String>? = null,
        taskStatuses: List<String>? = null,
        fromFacilityIds: List<String>? = null,
        toFacilityIds: List<String>? = null,
    ): Completable

    fun resetFilterAndSort(): Completable
    fun saveSessionCache(): Completable
    fun resetToSessionCache(): Completable
}

class TasksManagerImpl : TasksManager {
    private val filterAndSort = InMemoryObjectCache(TasksFilterAndSort())
    private val filterAndSortSessionCache = InMemoryObjectCache(TasksFilterAndSort())

    override fun getCurrentFilterAndSort(): Single<TasksFilterAndSort> = filterAndSort.get()

    override fun getFilterAndSortObservable(): Observable<TasksFilterAndSort> =
        filterAndSort.getObservable()

    override fun getFilterCountObservable(): Observable<Int> =
        filterAndSort.getObservable().map { tasksFilterAndSort ->
            var count = 0
            if (tasksFilterAndSort.sortCategory != SortCategory.defaultCategory()) count++
            if (tasksFilterAndSort.projectFilter.isNotEmpty()) count++
            if (tasksFilterAndSort.taskFilter.isNotEmpty()) count++
            if (tasksFilterAndSort.statusFilter.isNotEmpty()) count++
            if (tasksFilterAndSort.fromFilter.isNotEmpty()) count++
            if (tasksFilterAndSort.toFilter.isNotEmpty()) count++
            count
        }

    override fun updateFilterAndSort(
        newCategory: SortCategory?,
        projectIds: List<String>?,
        taskTypes: List<String>?,
        taskStatuses: List<String>?,
        fromFacilityIds: List<String>?,
        toFacilityIds: List<String>?,
    ): Completable = filterAndSort.edit {
        if (newCategory != null && newCategory != sortCategory) sortCategory = newCategory
        projectIds?.let { projectFilter = it }
        taskTypes?.let { taskFilter = it }
        taskStatuses?.let { statusFilter = it }
        fromFacilityIds?.let { fromFilter = it }
        toFacilityIds?.let { toFilter = it }
        this
    }

    override fun resetFilterAndSort(): Completable = filterAndSort.edit { TasksFilterAndSort() }

    override fun saveSessionCache(): Completable =
        filterAndSort.get().flatMapCompletable { filterAndSort ->
            filterAndSortSessionCache.put(filterAndSort.copy())
        }

    override fun resetToSessionCache(): Completable =
        filterAndSortSessionCache.get().flatMapCompletable { filterAndSortSessionCache ->
            filterAndSort.put(filterAndSortSessionCache.copy())
        }
}