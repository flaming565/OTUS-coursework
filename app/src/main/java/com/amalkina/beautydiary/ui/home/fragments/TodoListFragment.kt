package com.amalkina.beautydiary.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.amalkina.beautydiary.BR
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.databinding.FragmentTodoListBinding
import com.amalkina.beautydiary.ui.common.fragments.BaseFragment
import com.amalkina.beautydiary.ui.home.models.TodoCategory
import com.amalkina.beautydiary.ui.home.vm.TodoListViewModel
import com.amalkina.beautydiary.ui.tasks.models.CategoryTask
import com.amalkina.beautydiary.ui.tasks.models.UserTaskAction
import com.github.akvast.mvvm.adapter.ViewModelAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel


class TodoListFragment : BaseFragment() {

    private val viewModel by viewModel<TodoListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTodoListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            appbar.toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }

        val periodAdapter = ArrayAdapter(
            requireContext(),
            R.layout.cell_todo_list_dropdown_item,
            viewModel.periodNames
        )
        binding.periodSelector.apply {
            setAdapter(periodAdapter)
            setText(viewModel.currentPeriod.value.name, false)
            onItemClickListener = OnItemClickListener { _, _, position, _ ->
                viewModel.updateSelectedPeriod(position)
            }
        }

        val groupAdapter = ArrayAdapter(
            requireContext(),
            R.layout.cell_todo_list_dropdown_item,
            viewModel.groupNames
        )
        binding.groupSelector.apply {
            setAdapter(groupAdapter)
            setText(viewModel.currentGroup.value.title, false)
            onItemClickListener = OnItemClickListener { _, _, position, _ ->
                viewModel.updateSelectedGroup(position)
            }
        }

        val taskAdapter = makeAdapter()
        binding.recyclerView.apply {
            adapter = taskAdapter
            itemAnimator = null
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todoListItems.collect {
                    val arrayItems = it.toTypedArray()
                    val isContentEquals = taskAdapter.items.contentEquals(arrayItems)
                    if (!isContentEquals) {
                        taskAdapter.items = arrayItems
                        if (it.size > 1 && viewModel.isRecycleViewAnimate) {
                            viewModel.isRecycleViewAnimate = false
                            binding.recyclerView.scheduleLayoutAnimation()
                        }
                    }
                }
            }
        }

        viewModel.userActionEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                if (!updateEventTimestamp()) return@let
                dialog?.dismiss()

                when (it) {
                    is UserTaskAction.OnClickTask -> findNavController().navigate(
                        TodoListFragmentDirections.openTaskDetail(it.id)
                    )
                    is UserTaskAction.EditTask -> findNavController().navigate(
                        TodoListFragmentDirections.openAddTask(it.categoryId, it.id)
                    )
                    is UserTaskAction.DeleteTask -> showDeleteTaskDialog(it.id, it.name)
                    else -> {
                    }
                }
            }
        }

        return binding.root
    }

    private fun makeAdapter() = object : ViewModelAdapter(viewLifecycleOwner) {
        init {
            sharedObject(viewModel, BR.todoList)
            cell(CategoryTask::class.java, R.layout.cell_todo_list_task_item, BR.vm)
            cell(TodoCategory::class.java, R.layout.cell_todo_list_category_item, BR.vm)
        }

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is CategoryTask && newItem is CategoryTask) {
                return oldItem.id == newItem.id
            }
            return super.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is CategoryTask && newItem is CategoryTask) {
                return oldItem.toString() == newItem.toString()
            }
            return super.areContentsTheSame(oldItem, newItem)
        }
    }

    private fun showDeleteTaskDialog(taskId: Long, taskName: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.task_list_delete_task_dialog_message, taskName))
            .setPositiveButton(R.string.common_delete) { _, _ ->
                viewModel.deleteTask(taskId)
            }
            .setNegativeButton(R.string.common_cancel, null)
            .show()
    }
}