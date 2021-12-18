package com.amalkina.beautydiary.ui.tasks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.databinding.FragmentTaskDetailBinding
import com.amalkina.beautydiary.ui.common.fragments.BaseFragment
import com.amalkina.beautydiary.ui.views.TaskProgressChart
import com.amalkina.beautydiary.ui.tasks.vm.TaskDetailViewModel
import com.google.android.material.datepicker.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class TaskDetailFragment : BaseFragment() {

    private val args by navArgs<TaskDetailFragmentArgs>()
    private val viewModel by viewModel<TaskDetailViewModel> { parametersOf(args.taskId) }

    private lateinit var progressChart: TaskProgressChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTaskDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            progressChart = chart

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.edit -> {
                        findNavController().navigate(
                            TaskDetailFragmentDirections.openEditTask(
                                taskId = args.taskId
                            )
                        )
                        true
                    }
                    R.id.delete -> {
                        showDeleteTaskDialog()
                        true
                    }
                    else -> false
                }
            }
        }

        viewModel.domainTask
            .onEach {
                it?.let { task -> progressChart.setTask(task) }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.taskCompleteEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                showCalendar(it.lastExecutionDate)
            }
        }

        return binding.root
    }

    private fun showDeleteTaskDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.task_detail_delete_task_dialog_message))
            .setPositiveButton(R.string.common_delete) { _, _ ->
                viewModel.deleteTask()
            }
            .setNegativeButton(R.string.common_cancel, null)
            .show()
    }

    private fun showCalendar(minDate: Long) {
        val calendarConstraints = CalendarConstraints.Builder()
            .setValidator(
                CompositeDateValidator.allOf(
                    listOf(
                        DateValidatorPointForward.from(minDate),
                        DateValidatorPointBackward.before(System.currentTimeMillis())
                    )
                )
            )
            .build()

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.MaterialCalendarTheme)
            .setTitleText(R.string.task_detail_select_date)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(calendarConstraints)
            .build()

        picker.addOnPositiveButtonClickListener {
            picker.selection?.let { date -> viewModel.completeTask(date) }
        }
        picker.show(parentFragmentManager, null)
    }
}


