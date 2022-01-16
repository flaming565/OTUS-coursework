package com.beautydiary.featurestatistics.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.beautydiary.core_ui.fragments.BaseFragment
import com.beautydiary.featurestatistics.databinding.FragmentStatisticsBinding
import com.beautydiary.featurestatistics.vm.StatisticsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.viewmodel.ext.android.viewModel


internal class StatisticsFragment : BaseFragment() {

    private val viewModel by viewModel<StatisticsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentStatisticsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel

            appbar.toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }

        viewModel.categories.onEach {
            binding.lineChartCategory.setCategories(it)
            binding.lineChartCommon.setCategories(it)
            binding.pieChartMonth.setCategories(it)
            binding.pieChartToday.setCategories(it)
        }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.currentDate.onEach {
            binding.lineChartCategory.setDate(it)
            binding.lineChartCommon.setDate(it)
            binding.pieChartMonth.setDate(it)
            binding.pieChartToday.setDate(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }
}