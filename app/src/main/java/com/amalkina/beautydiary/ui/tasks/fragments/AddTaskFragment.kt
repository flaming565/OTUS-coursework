package com.amalkina.beautydiary.ui.tasks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amalkina.beautydiary.databinding.FragmentAddTaskBinding
import com.amalkina.beautydiary.ui.common.fragments.BaseFragment
import com.amalkina.beautydiary.ui.tasks.vm.AddTaskViewModel
import com.weigan.loopview.LoopView
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
                findNavController().popBackStack()
            }
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
                if (!updateEventTimestamp()) return@let
                findNavController().popBackStack()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.tasksNames.collect {
                        initLoopView(binding.loopView, it)
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