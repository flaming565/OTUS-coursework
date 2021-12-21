package com.amalkina.beautydiary.ui.home.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.domain.common.Result
import com.amalkina.beautydiary.domain.models.DomainCategory
import com.amalkina.beautydiary.domain.models.DomainCategoryWithTasks
import com.amalkina.beautydiary.domain.models.DomainTask
import com.amalkina.beautydiary.domain.usecases.CategoryActionsUseCase
import com.amalkina.beautydiary.domain.usecases.TaskActionsUseCase
import com.amalkina.beautydiary.ui.common.ext.map
import com.amalkina.beautydiary.ui.common.ext.mapToMutable
import com.amalkina.beautydiary.ui.common.ext.toUIModel
import com.amalkina.beautydiary.ui.common.ext.tryCast
import com.amalkina.beautydiary.ui.common.models.BaseModel.Companion.getPlurals
import com.amalkina.beautydiary.ui.common.models.BaseModel.Companion.getString
import com.amalkina.beautydiary.ui.common.utils.Event
import com.amalkina.beautydiary.ui.common.vm.BaseViewModel
import com.amalkina.beautydiary.ui.home.models.TodoCategory
import com.amalkina.beautydiary.ui.tasks.models.UserTaskAction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.full.isSubclassOf

internal class TodoListViewModel : BaseViewModel() {

    private val taskUseCase by inject<TaskActionsUseCase>()
    private val categoryUseCase by inject<CategoryActionsUseCase>()

    private val periods = Period.values().sortedBy { it.value }.drop(1) + Period.All
    val periodNames = periods.map { it.name }
    val currentPeriod: MutableStateFlow<Period> = MutableStateFlow(Period.Today)

    private val groups = Group.values()
    val groupNames = groups.map { it.title }
    val currentGroup: MutableStateFlow<Group> = MutableStateFlow(Group.BY_CATEGORY)

    private val fullCategoriesMap = categoryUseCase.allWithTasks()
        .flatMapMerge { mapGetCategoriesMapValues(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyMap()
        )
    private val allTasks = fullCategoriesMap.mapToMutable(viewModelScope) { fullMap ->
        fullMap.values.fold(listOf<DomainTask>()) { acc, items -> acc.toMutableList() + items }
    }

    val todoListItems = fullCategoriesMap.mapToMutable(viewModelScope) { getPeriodItems(it) }
    val showPlaceholder = todoListItems.map(viewModelScope) { it.isEmpty() }

    val userActionEvent = MutableLiveData<Event<UserTaskAction>>()

    fun updateSelectedPeriod(position: Int) {
        if (position < periods.size && currentPeriod.value != periods[position]) {
            currentPeriod.value = periods[position]
            updatePeriodItems(periods[position])
        }
    }

    fun updateSelectedGroup(position: Int) {
        if (position < groupNames.size && currentGroup.value != groups[position]) {
            currentGroup.value = groups[position]
            todoListItems.value = getPeriodItems()
        }
    }

    fun onTaskClick(id: Long) {
        userActionEvent.postValue(Event(UserTaskAction.OnClickTask(id)))
    }

    fun onEditTask(id: Long, categoryId: Long) {
        userActionEvent.postValue(Event(UserTaskAction.EditTask(id, categoryId)))
    }

    fun onDeleteTask(id: Long, name: String) {
        userActionEvent.postValue(Event(UserTaskAction.DeleteTask(id, name)))
    }

    fun onCompleteTask(id: Long) {
        getTaskById(id)?.let {
            completeTask(it)
        }
    }

    fun deleteTask(id: Long) {
        getTaskById(id)?.let {
            deleteTask(it)
        }
    }

    private fun getTaskById(id: Long): DomainTask? {
        return allTasks.value.firstOrNull { it.id == id }
    }

    private fun completeTask(task: DomainTask) {
        launch {
            task.executionDateList.add(System.currentTimeMillis())
            val result = taskUseCase.update(task)
            mapResponseResult(result)
        }
    }

    private fun deleteTask(task: DomainTask) {
        launch {
            val result = taskUseCase.delete(task)
            mapResponseResult(result)
        }
    }

    private fun mapGetCategoriesMapValues(result: Result): Flow<Map<DomainCategory, List<DomainTask>>> {
        var categoriesMap: Flow<Map<DomainCategory, List<DomainTask>>> = flowOf(emptyMap())
        mapResponseResult(result)?.let {
            tryCast<Flow<List<DomainCategoryWithTasks>>>(it) {
                categoriesMap = this.transform { list ->
                    emit(list.associate { categoryWithTasks ->
                        categoryWithTasks.category to categoryWithTasks.tasks.map { task -> task }
                    })
                }
            }
        }
        return categoriesMap
    }

    private fun updatePeriodItems(period: Period) {
        todoListItems.value =
            getPeriodItems(period = period, categoriesMap = fullCategoriesMap.value)
    }

    private fun getPeriodItems(categoriesMap: Map<DomainCategory, List<DomainTask>>): List<Any> {
        return getPeriodItems(period = currentPeriod.value, categoriesMap = categoriesMap)
    }

    private fun getPeriodItems(
        period: Period = currentPeriod.value,
        categoriesMap: Map<DomainCategory, List<DomainTask>> = fullCategoriesMap.value
    ) = when (currentGroup.value) {
        Group.BY_CATEGORY -> getGroupByCategoryItems(period, categoriesMap)
        Group.BY_DATE -> getGroupByDateItems(period)
    }

    private fun getGroupByCategoryItems(
        period: Period,
        categoriesMap: Map<DomainCategory, List<DomainTask>>
    ): MutableList<Any> {
        val result = mutableListOf<Any>()

        if (period == Period.All)
            categoriesMap.onEachIndexed { index, (category, tasks) ->
                result.add(TodoCategory(index.toLong(), category.name, "${tasks.size}"))
                tasks.forEach { result.add(it.toUIModel()) }
            }
        else
            categoriesMap.onEachIndexed { index, (category, tasks) ->
                val periodTasks = tasks.filter { it.daysRemaining <= period.value }
                if (periodTasks.isNotEmpty()) {
                    result.add(TodoCategory(index.toLong(), category.name, "${periodTasks.size}"))
                    periodTasks.forEach { result.add(it.toUIModel()) }
                }
            }

        return result
    }

    private fun getGroupByDateItems(period: Period): MutableList<Any> {
        val result = mutableListOf<Any>()

        var dateTasks = allTasks.value.map {
            val task = it.toUIModel()
            if (task.daysRemaining < 0) task.daysRemaining = 0
            task
        }.groupBy { it.daysRemaining }

        if (period != Period.All)
            dateTasks = dateTasks.filter { (key, _) -> key <= period.value }

        dateTasks.toSortedMap()
            .onEachIndexed { index, (days, tasks) ->
                if (period != Period.Today)
                    result.add(
                        TodoCategory(-(index + 1).toLong(), getDateHeader(days), "${tasks.size}")
                    )
                tasks.forEach { result.add(it) }
            }

        return result
    }

    private fun getDateHeader(value: Int): String {
        if (value == 0) return getString(R.string.common_today)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, value)
        return SimpleDateFormat("dd MMM yyyy (EEEE)", Locale.getDefault())
            .format(calendar.timeInMillis)
    }

    sealed class Period(val value: Int) {
        object Today : Period(0)
        object OneDay : Period(1)
        object TwoDays : Period(2)
        object ThreeDays : Period(3)
        object OneWeek : Period(7)
        object TwoWeek : Period(14)
        object All : Period(-1)

        val name = when {
            value < 0 -> getString(R.string.todo_list_period_all)
            value == 0 -> getString(R.string.todo_list_period_today)
            value % 7 == 0 -> getPlurals(
                R.plurals.todo_list_period_weeks,
                value / 7,
                value / 7
            )
            else -> getPlurals(R.plurals.todo_list_period_days, value, value)
        }

        companion object {
            private val map = Period::class.nestedClasses
                .filter { klass -> klass.isSubclassOf(Period::class) }
                .map { klass -> klass.objectInstance }
                .filterIsInstance<Period>()
                .associateBy { value -> value.name }

            fun values() = map.values
        }
    }

    enum class Group(val title: String) {
        BY_DATE(getString(R.string.todo_list_group_by_date)),
        BY_CATEGORY(getString(R.string.todo_list_group_by_category));
    }

}