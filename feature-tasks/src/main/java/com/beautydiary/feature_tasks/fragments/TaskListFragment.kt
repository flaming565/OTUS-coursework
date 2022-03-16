package com.beautydiary.feature_tasks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.beautydiary.core_ui.fragments.RecyclerViewFragment
import com.beautydiary.feature_tasks.BR
import com.beautydiary.feature_tasks.R
import com.beautydiary.feature_tasks.databinding.DialogDeleteTaskAlertBinding
import com.beautydiary.feature_tasks.databinding.FragmentTaskListBinding
import com.beautydiary.feature_tasks.models.CategoryTask
import com.beautydiary.feature_tasks.models.CategoryTaskNew
import com.beautydiary.feature_tasks.models.TaskItem
import com.beautydiary.feature_tasks.models.UserTaskAction
import com.beautydiary.feature_tasks.vm.TaskListViewModel
import com.github.akvast.mvvm.adapter.ViewModelAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class TaskListFragment : RecyclerViewFragment() {
    private val args by navArgs<TaskListFragmentArgs>()
    private val viewModel by viewModel<TaskListViewModel> { parametersOf(args.category) }

    private var binding: FragmentTaskListBinding? = null

    override fun getRecyclerView(): RecyclerView? = binding?.recyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.sort_by_name -> {
                        viewModel.sortByName()
                        true
                    }
                    R.id.sort_by_due_date -> {
                        viewModel.sortByDueDate()
                        true
                    }
                    R.id.sort_by_creation_date -> {
                        viewModel.sortByCreationDate()
                        true
                    }
                    R.id.sort_by_priority -> {
                        viewModel.sortByPriority()
                        true
                    }
                    R.id.add_task -> {
                        findNavController().navigate(TaskListFragmentDirections.openAddTask(args.category.id))
                        true
                    }
                    else -> false
                }
            }
        }

        rvAdapter = makeAdapter()
        binding?.recyclerView?.apply {
            adapter = rvAdapter
            itemAnimator = DefaultItemAnimator()
        }

        viewModel.allTasks
            .onEach { items ->
                items?.let {
                    rvAdapter?.items = it as Array<Any>
                    startListAnimation()
                }
            }
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.isSortOptionAvailable
            .onEach { binding?.toolbar?.menu?.get(0)?.isVisible = it }
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.userActionEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                if (!updateEventTimestamp()) return@let
                dialog?.dismiss()

                when (it) {
                    is UserTaskAction.OnClickTask -> findNavController().navigate(
                        TaskListFragmentDirections.openTaskDetail(it.id)
                    )
                    is UserTaskAction.AddTask -> findNavController().navigate(
                        TaskListFragmentDirections.openAddTask(it.categoryId)
                    )
                    is UserTaskAction.EditTask -> findNavController().navigate(
                        TaskListFragmentDirections.openAddTask(it.categoryId, it.id)
                    )
                    is UserTaskAction.DeleteTask -> showDeleteTaskDialog(it.id, it.name)
                }
            }
        }

        return binding?.root
    }

    private fun makeAdapter() = object : ViewModelAdapter(viewLifecycleOwner) {
        init {
            setHasStableIds(true)

            sharedObject(viewModel, BR.taskList)
            cell(CategoryTask::class.java, R.layout.cell_task_list_card, BR.vm)
            cell(CategoryTaskNew::class.java, R.layout.cell_task_list_card_new, BR.vm)
        }

        override fun getItemId(position: Int): Long {
            return when (val item = items[position]) {
                is TaskItem -> item.id
                else -> super.getItemId(position)
            }
        }

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is TaskItem && newItem is TaskItem)
                return oldItem.id == newItem.id

            return super.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is CategoryTask && newItem is CategoryTask)
                return oldItem.toString() == newItem.toString()

            return super.areContentsTheSame(oldItem, newItem)
        }
    }

    private fun showDeleteTaskDialog(taskId: Long, taskName: String) {
        val dialogBinding = DialogDeleteTaskAlertBinding.inflate(layoutInflater).apply {
            message = getString(R.string.task_list_delete_task_dialog_message, taskName)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.common_delete) { _, _ ->
                viewModel.deleteTask(taskId)
                viewModel.toggleShowDeleteDialog(dialogBinding.checkbox.isChecked)
            }
            .setNegativeButton(R.string.common_cancel, null)
            .show()
    }

}