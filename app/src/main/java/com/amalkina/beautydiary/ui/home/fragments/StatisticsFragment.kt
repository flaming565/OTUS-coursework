package com.amalkina.beautydiary.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amalkina.beautydiary.databinding.FragmentStatisticsBinding
import com.amalkina.beautydiary.ui.common.fragments.BaseFragment
import com.amalkina.beautydiary.ui.home.vm.StatisticsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.viewmodel.ext.android.viewModel


class StatisticsFragment : BaseFragment() {

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