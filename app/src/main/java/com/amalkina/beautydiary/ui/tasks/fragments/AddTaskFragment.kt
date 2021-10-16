package com.amalkina.beautydiary.ui.tasks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.amalkina.beautydiary.ui.common.fragments.BaseFragment
import com.amalkina.beautydiary.databinding.FragmentAddTaskBinding
import com.amalkina.beautydiary.ui.tasks.vm.AddTaskViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class AddTaskFragment : BaseFragment() {
    private val args by navArgs<AddTaskFragmentArgs>()
    private val viewModel by viewModel<AddTaskViewModel> { parametersOf(args.categoryId, args.taskId) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentAddTaskBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        binding.loopView.setListener { index ->
            viewModel.currentIndex.value = index
        }

        binding.etTaskFrequency.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty())
                binding.etTaskFrequency.append("0")
            else {
                val formatString = text.toString().toInt().toString()
                if (text.toString() != formatString) {
                    binding.etTaskFrequency.setText(formatString)
                    binding.etTaskFrequency.setSelection(formatString.length)
                }
            }
        }

        viewModel.saveTaskEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                requireActivity().onBackPressed()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.tasksNames.collect {
                        binding.loopView.setItems(it)
                        binding.loopView.setInitPosition(0)
                        viewModel.currentIndex.value = 0
                    }
                }

                launch {
                    viewModel.currentTask.collect { task ->
                        task?.let {
                            viewModel.updateTaskFields(task)
                        }
                    }
                }

                launch {
                    viewModel.taskPriority.collect {
                        if (it < 1F)
                            binding.ratingBar.rating = 1F
                    }
                }
            }
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                showErrorDialog(requireContext(), it)
            }
        }

        return binding.root
    }
}