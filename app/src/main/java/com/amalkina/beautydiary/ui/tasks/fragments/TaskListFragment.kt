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
import com.amalkina.beautydiary.ui.tasks.models.TaskItem
import com.amalkina.beautydiary.ui.tasks.vm.TaskListViewModel
import com.github.akvast.mvvm.adapter.ViewModelAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class TaskListFragment : BaseFragment() {
    private val args by navArgs<TaskListFragmentArgs>()
    private val viewModel by viewModel<TaskListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentTaskListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            appbar.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        }

        val taskAdapter = makeAdapter()
        binding.recyclerView.adapter = taskAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect {
                    taskAdapter.items = it.toTypedArray()
                }
            }
        }

        viewModel.addTaskEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                if (!updateEventTimestamp()) return@let
                findNavController().navigate(TaskListFragmentDirections.openAddTask(args.categoryId))
            }
        }

        return binding.root
    }

    private fun makeAdapter() = object : ViewModelAdapter(viewLifecycleOwner) {
        init {
            sharedObject(viewModel, BR.taskList)
            cell(TaskItem.Task::class.java, R.layout.cell_task_list_card, BR.vm)
            cell(TaskItem.New::class.java, R.layout.cell_task_list_card_new, BR.vm)
        }
    }
}