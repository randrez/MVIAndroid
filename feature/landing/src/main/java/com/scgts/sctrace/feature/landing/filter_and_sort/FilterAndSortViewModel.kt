package com.scgts.sctrace.feature.landing.filter_and_sort

import androidx.lifecycle.ViewModel
import com.scgts.framework.mvi.MviViewModel
import com.scgts.framework.mvi.intentsBuild
import com.scgts.sctrace.base.model.*
import com.scgts.sctrace.base.model.FilterAndSortCategory.*
import com.scgts.sctrace.base.model.FilterAndSortCategory.Project
import com.scgts.sctrace.base.model.FilterAndSortCategory.TaskType
import com.scgts.sctrace.base.model.FilterAndSortOption.*
import com.scgts.sctrace.base.model.TaskTypeForFiltering.*
import com.scgts.sctrace.feature.landing.filter_and_sort.FilterAndSortMvi.Intent
import com.scgts.sctrace.feature.landing.filter_and_sort.FilterAndSortMvi.Intent.*
import com.scgts.sctrace.feature.landing.filter_and_sort.FilterAndSortMvi.ViewState
import com.scgts.sctrace.framework.navigation.AppNavigator
import com.scgts.sctrace.tasks.TasksRepository
import com.scgts.sctrace.user.UserRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier
import util.TasksManager

class FilterAndSortViewModel(
    private val tasksManager: TasksManager,
    private val tasksRepository: TasksRepository,
    private val userRepository: UserRepository,
    private val navigator: AppNavigator,
) : ViewModel(), MviViewModel<Intent, ViewState> {
    private val initialState = Supplier { ViewState() }

    private val reducer = BiFunction<ViewState, Intent, ViewState> { prev, intent ->
        when (intent) {
            is BackClicked -> prev.copy(selectedCategory = None)
            is CategorySelected -> prev.copy(selectedCategory = intent.category)
            is UpdateCategories -> {
                val categories = prev.categories.toMutableList()
                val index = categories.indexOfFirst { it::class == intent.category::class }
                categories[index] = intent.category
                val selectedCategory =
                    if (prev.selectedCategory::class == intent.category::class) intent.category else prev.selectedCategory
                prev.copy(categories = categories, selectedCategory = selectedCategory)
            }
            is SetNumOfFilteredTasks -> prev.copy(numOfFilteredTasks = intent.numOfTasks)
            is SetClearAllEnability -> prev.copy(clearAllEnabled = intent.enabled)
            else -> prev
        }
    }

    override fun bind(intents: Observable<Intent>): Observable<ViewState> {
        return bindIntents(intents)
            .scanWith(initialState, reducer)
            .distinctUntilChanged()
    }

    private fun bindIntents(intents: Observable<Intent>): Observable<Intent> {
        return intentsBuild(intents) {
            dataIntent(tasksManager.saveSessionCache())

            dataIntent(tasksManager.getFilterAndSortObservable()) {
                it.map { tasksFilterAndSort ->
                    val sortOptions = SortCategory.toList().map { category ->
                        SortOption(
                            sortName = category.uiName,
                            selected = category == tasksFilterAndSort.sortCategory,
                            category = category,
                        )
                    }
                    UpdateCategories(Sort(sortOptions))
                }
            }

            dataIntent(tasksManager.getFilterAndSortObservable()) {
                it.flatMap { tasksFilterAndSort ->
                    tasksRepository.getTasksCountByProject(
                        tasksFilterAndSort.copy(projectFilter = emptyList())
                    ).flatMapObservable { list ->
                        val filterOptions = list.map { project ->
                            FilterOption(
                                filterName = project.name,
                                selected = tasksFilterAndSort.projectFilter.contains(project.id),
                                numOfTasks = project.numOfTasks,
                                filterId = project.id
                            )
                        }
                        Observable.just(UpdateCategories(Project(filterOptions)))
                    }
                }
            }

            val filtersAndUserRole = Observable.combineLatest(
                tasksManager.getFilterAndSortObservable(),
                userRepository.getUserRolesForAllProject(),
                { filters, user -> Pair(filters, user) }
            )

            dataIntent(filtersAndUserRole) {
                it.flatMap { (tasksFilterAndSort, user) ->
                    tasksRepository.getTasksCountByTaskType(
                        tasksFilterAndSort.copy(taskFilter = emptyList())
                    ).flatMapObservable { list ->
                        val filterOptions =
                            getListOfTaskTypeOptionsPerUserRole(user).map { taskType ->
                                val typeWithNumOfTasks =
                                    list.find { typeWithNumOfTasks -> typeWithNumOfTasks.type == taskType }
                                FilterOption(
                                    filterName = taskType.displayName,
                                    selected = tasksFilterAndSort.taskFilter.contains(taskType.ordinal.toString()),
                                    numOfTasks = typeWithNumOfTasks?.numOfTasks ?: 0,
                                    filterId = taskType.ordinal.toString()
                                )
                            }
                        Observable.just(UpdateCategories(TaskType(filterOptions)))
                    }
                }
            }

            dataIntent(tasksManager.getFilterAndSortObservable()) {
                it.flatMap { tasksFilterAndSort ->
                    tasksRepository.getTasksCountByStatus(
                        tasksFilterAndSort.copy(statusFilter = emptyList())
                    ).flatMapObservable { list ->
                        val filterOptions = TaskStatus.toList().map { taskStatus ->
                            val statusWithNumOfTasks =
                                list.find { statusWithNumOfTasks -> statusWithNumOfTasks.status == taskStatus }
                            StatusFilterOption(
                                status = taskStatus,
                                selected = tasksFilterAndSort.statusFilter.contains(taskStatus.dbName),
                                numOfTasks = statusWithNumOfTasks?.numOfTasks ?: 0,
                            )
                        }
                        Observable.just(UpdateCategories(Status(filterOptions)))
                    }
                }
            }

            dataIntent(tasksManager.getFilterAndSortObservable()) {
                it.flatMap { tasksFilterAndSort ->
                    tasksRepository.getTasksCountByLocation(
                        fromLocation = true,
                        tasksFilter = tasksFilterAndSort.copy(fromFilter = emptyList())
                    ).flatMapObservable { list ->
                        val filterOptions = list.map { facility ->
                            LocationFilterOption(
                                locationName = facility.name,
                                selected = tasksFilterAndSort.fromFilter.contains(facility.id),
                                facilityType = facility.facilityType,
                                numOfTasks = facility.numOfTasks,
                                facilityId = facility.id,
                            )
                        }
                        Observable.just(UpdateCategories(FromLocation(filterOptions)))
                    }
                }
            }

            dataIntent(tasksManager.getFilterAndSortObservable()) {
                it.flatMap { tasksFilterAndSort ->
                    tasksRepository.getTasksCountByLocation(
                        fromLocation = false,
                        tasksFilter = tasksFilterAndSort.copy(toFilter = emptyList())
                    ).flatMapObservable { list ->
                        val filterOptions = list.map { facility ->
                            LocationFilterOption(
                                locationName = facility.name,
                                selected = tasksFilterAndSort.toFilter.contains(facility.id),
                                facilityType = facility.facilityType,
                                numOfTasks = facility.numOfTasks,
                                facilityId = facility.id,
                            )
                        }
                        Observable.just(UpdateCategories(ToLocation(filterOptions)))
                    }
                }
            }

            dataIntent(tasksManager.getFilterAndSortObservable()) {
                it.flatMap { filterAndSort ->
                    tasksRepository.getFilteredTasks(filterAndSort).flatMapObservable { tasks ->
                        Observable.just(SetNumOfFilteredTasks(numOfTasks = tasks.size))
                    }
                }
            }

            dataIntent(tasksManager.getFilterCountObservable()) {
                it.map { filterCount -> SetClearAllEnability(filterCount > 0) }
            }

            viewIntentCompletable<CloseClicked> {
                it.flatMapCompletable {
                    tasksManager.resetToSessionCache().andThen(navigator.popBackStack())
                }
            }

            viewIntentCompletable<ClearClicked> {
                it.flatMapCompletable { intent ->
                    when (intent.category) {
                        is None -> tasksManager.resetFilterAndSort()
                        is Sort -> tasksManager.updateFilterAndSort(newCategory = SortCategory.defaultCategory())
                        is Project -> tasksManager.updateFilterAndSort(projectIds = emptyList())
                        is TaskType -> tasksManager.updateFilterAndSort(taskTypes = emptyList())
                        is Status -> tasksManager.updateFilterAndSort(taskStatuses = emptyList())
                        is FromLocation -> tasksManager.updateFilterAndSort(fromFacilityIds = emptyList())
                        is ToLocation -> tasksManager.updateFilterAndSort(toFacilityIds = emptyList())
                    }
                }
            }

            viewIntentCompletable<OptionClicked> {
                it.flatMapCompletable { (selectedCategory, option) ->
                    tasksManager.getCurrentFilterAndSort().flatMapCompletable { filters ->
                        when (selectedCategory) {
                            is None -> Completable.complete()
                            is Sort -> {
                                val optionCategory = (option as SortOption).category
                                if (filters.sortCategory == optionCategory) Completable.complete()
                                else tasksManager.updateFilterAndSort(newCategory = optionCategory)
                            }
                            is Project -> tasksManager.updateFilterAndSort(
                                projectIds = updateFilter(filters.projectFilter, option)
                            )
                            is TaskType -> tasksManager.updateFilterAndSort(
                                taskTypes = updateFilter(filters.taskFilter, option)
                            )
                            is Status -> tasksManager.updateFilterAndSort(
                                taskStatuses = updateFilter(filters.statusFilter, option)
                            )
                            is FromLocation -> tasksManager.updateFilterAndSort(
                                fromFacilityIds = updateFilter(filters.fromFilter, option)
                            )
                            is ToLocation -> tasksManager.updateFilterAndSort(
                                toFacilityIds = updateFilter(filters.toFilter, option)
                            )
                        }
                    }
                }
            }

            viewIntentCompletable<ShowTasksClicked> {
                it.flatMapCompletable { navigator.popBackStack() }
            }

            viewIntentPassThroughs(BackClicked::class, CategorySelected::class)
        }
    }

    private fun getListOfTaskTypeOptionsPerUserRole(user: UserRole): List<TaskTypeForFiltering> {
        val taskTypeOptions: MutableList<TaskTypeForFiltering> = mutableListOf()
        if (user.isDrillingEngineer) taskTypeOptions.addAll(DRILLING_ENGINEER_TASKS)
        if (user.isYardOperator) taskTypeOptions.addAll(YARD_OPERATOR_TASKS)
        return taskTypeOptions.distinct().sortedBy { it.displayName }
    }

    private fun updateFilter(filter: List<String>, option: FilterAndSortOption): List<String> {
        val id = when (option) {
            is FilterOption -> option.filterId
            is StatusFilterOption -> option.status.dbName
            is LocationFilterOption -> option.facilityId
            else -> ""
        }
        val mutableList = filter.toMutableList()
        if (option.checked) mutableList.remove(id)
        else mutableList.add(id)
        return mutableList
    }

    companion object {
        val DRILLING_ENGINEER_TASKS = listOf(
            AD_HOC,
            CONSUME,
            DISPATCH,
            INBOUND_TO_WELL,
        )
        val YARD_OPERATOR_TASKS = listOf(
            AD_HOC,
            BUILD_ORDER,
            DISPATCH,
            INBOUND_FROM_MILL,
            INBOUND_FROM_WELL_SITE,
            RACK_TRANSFER,
        )
    }
}