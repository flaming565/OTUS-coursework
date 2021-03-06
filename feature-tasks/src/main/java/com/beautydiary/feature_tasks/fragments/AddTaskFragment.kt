package com.beautydiary.feature_tasks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.beautydiary.core_ui.fragments.BaseFragment
import com.beautydiary.feature_tasks.databinding.FragmentAddTaskBinding
import com.beautydiary.feature_tasks.vm.AddTaskViewModel
import com.weigan.loopview.LoopView
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
                findNavController().popBackStack()
            }
        }

        binding.etTaskFrequency.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                binding.etTaskFrequency.append("0")
            } else {
                val formatString = text.toString().toInt().toString()
                if (text.toString() != formatString) {
                    binding.etTaskFrequency.setText(formatString)
                    binding.etTaskFrequency.setSelection(formatString.length)
                }
            }
        }

        viewModel.saveTaskEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                if (!updateEventTimestamp()) return@let
                findNavController().popBackStack()
            }
        }

        viewModel.tasksNames.observe {
            initLoopView(binding.loopView, it)
        }

        viewModel.taskPriority.observe {
            if (it < 1F) binding.ratingBar.rating = 1F
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                showErrorDialog(requireContext(), it)
            }
        }

        return binding.root
    }

    private fun initLoopView(loopView: LoopView, items: List<String>) {
        if (items.isNotEmpty() && !viewModel.isEditMode) {
            loopView.apply {
                setItems(items)
                setInitPosition(0)
                setListener { index ->
                    viewModel.currentIndex.value = index
                }
            }
            viewModel.currentIndex.value = 0
        }
    }
}