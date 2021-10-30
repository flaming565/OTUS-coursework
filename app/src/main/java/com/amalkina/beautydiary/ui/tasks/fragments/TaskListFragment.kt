package com.amalkina.beautydiary.ui.tasks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amalkina.beautydiary.BR
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.ui.common.fragments.BaseFragment
import com.amalkina.beautydiary.databinding.FragmentTaskListBinding
import com.amalkina.beautydiary.ui.tasks.models.CategoryTask
import com.amalkina.beautydiary.ui.tasks.models.CategoryTaskNew
import com.amalkina.beautydiary.ui.tasks.vm.TaskListViewModel
import com.amalkina.beautydiary.ui.tasks.vm.TaskListViewModel.UserAction
import com.github.akvast.mvvm.adapter.ViewModelAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import androidx.recyclerview.widget.DefaultItemAnimator


class TaskListFragment : BaseFragment() {
    private val args by navArgs<TaskListFragmentArgs>()
    private val viewModel by viewModel<TaskListViewModel> { parametersOf(args.category) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentTaskListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            appbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        }

        val taskAdapter = makeAdapter()
        binding.recyclerView.apply {
            adapter = taskAdapter
            itemAnimator = null
        }

        var isFirstLayoutAnimation = true
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoryTasks.collect {
                    taskAdapter.items = it.toTypedArray()
                    if (it.size > 1 && isFirstLayoutAnimation) {
                        binding.recyclerView.scheduleLayoutAnimation()
                        isFirstLayoutAnimation = false
                    }
                }
            }
        }

        viewModel.userActionEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                if (!updateEventTimestamp()) return@let
                dialog?.dismiss()

                when (it) {
                    is UserAction.OnClickTask -> {
                        // todo: navigate to task fragment
                    }
                    is UserAction.AddTask -> findNavController().navigate(TaskListFragmentDirections.openAddTask(it.categoryId))
                    is UserAction.EditTask -> findNavController().navigate(
                        TaskListFragmentDirections.openAddTask(it.categoryId, it.id)
                    )
                    is UserAction.DeleteTask -> showDeleteCategoryDialog(it.id, it.name)
                }
            }
        }

        return binding.root
    }

    private fun makeAdapter() = object : ViewModelAdapter(viewLifecycleOwner) {
        init {
            sharedObject(viewModel, BR.taskList)
            cell(CategoryTask::class.java, R.layout.cell_task_list_card, BR.vm)
            cell(CategoryTaskNew::class.java, R.layout.cell_task_list_card_new, BR.vm)
        }

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            if(oldItem is CategoryTask && newItem is CategoryTask) {
                return oldItem.id == newItem.id
            }
            return super.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            if(oldItem is CategoryTask && newItem is CategoryTask) {
                return oldItem.toString() == newItem.toString()
            }
            return super.areContentsTheSame(oldItem, newItem)
        }
    }

    private fun showDeleteCategoryDialog(taskId: Long, taskName: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.task_list_delete_task_dialog_message, taskName))
            .setPositiveButton(R.string.home_delete_category) { _, _ ->
                viewModel.deleteTask(taskId)
            }
            .setNegativeButton(R.string.common_cancel, null)
            .show()
    }
}