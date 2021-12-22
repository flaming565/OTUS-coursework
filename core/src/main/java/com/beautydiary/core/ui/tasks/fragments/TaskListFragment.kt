package com.beautydiary.core.ui.tasks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.beautydiary.core.BR
import com.beautydiary.core.R
import com.beautydiary.core.databinding.FragmentTaskListBinding
import com.beautydiary.core.ui.common.fragments.RecyclerViewFragment
import com.beautydiary.core.ui.tasks.models.CategoryTask
import com.beautydiary.core.ui.tasks.models.CategoryTaskNew
import com.beautydiary.core.ui.tasks.models.TaskItem
import com.beautydiary.core.ui.tasks.models.UserTaskAction
import com.beautydiary.core.ui.tasks.vm.TaskListViewModel
import com.github.akvast.mvvm.adapter.ViewModelAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allTasks.collect { items ->
                    items?.let {
                        rvAdapter?.items = it as Array<Any>
                        startListAnimation()
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
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.task_list_delete_task_dialog_message, taskName))
            .setPositiveButton(R.string.common_delete) { _, _ ->
                viewModel.deleteTask(taskId)
            }
            .setNegativeButton(R.string.common_cancel, null)
            .show()
    }

}