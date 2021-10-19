package com.amalkina.beautydiary.ui.home.fragments

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.amalkina.beautydiary.BR
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.databinding.DialogHomeSelectCategoryOptionsBinding
import com.amalkina.beautydiary.databinding.FragmentHomeBinding
import com.amalkina.beautydiary.ui.common.fragments.BaseFragment
import com.amalkina.beautydiary.ui.home.models.HomeCategory
import com.amalkina.beautydiary.ui.home.models.HomeCategoryNew
import com.amalkina.beautydiary.ui.home.vm.HomeViewModel
import com.amalkina.beautydiary.ui.home.vm.HomeViewModel.UserActionItem
import com.github.akvast.mvvm.adapter.ViewModelAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment() {
    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
        }

        val categoryAdapter = makeAdapter()
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = categoryAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collect {
                    categoryAdapter.items = it.toTypedArray()
                }
            }
        }

        viewModel.userActionEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                if (!updateEventTimestamp()) return@let
                dialog?.dismiss()

                when (it) {
                    is UserActionItem.OnClickCategory -> findNavController().navigate(
                        HomeFragmentDirections.openTaskListFragment(it.id)
                    )
                    is UserActionItem.OnLongClickCategory -> showCategoryOptionsDialog()
                    is UserActionItem.AddCategory -> findNavController().navigate(HomeFragmentDirections.openAddCategory())
                    is UserActionItem.EditCategory -> viewModel.selectedCategory?.let { category ->
                        findNavController().navigate(HomeFragmentDirections.openAddCategory(category.id))
                    }
                    is UserActionItem.DeleteCategory -> showDeleteCategoryDialog()
                }
            }
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                // todo: error processing
            }
        }

        return binding.root
    }

    private fun makeAdapter() = object : ViewModelAdapter(viewLifecycleOwner) {
        init {
            sharedObject(viewModel, BR.home)
            cell(HomeCategory::class.java, R.layout.cell_home_category, BR.vm)
            cell(HomeCategoryNew::class.java, R.layout.cell_home_category_new, BR.vm)
        }
    }

    private fun showCategoryOptionsDialog() {
        val dialogBinding = DialogHomeSelectCategoryOptionsBinding.inflate(layoutInflater).apply {
            vm = viewModel
        }
        dialog?.dismiss()
        dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .show()
    }

    private fun showDeleteCategoryDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.home_delete_category_dialog_message)
            .setPositiveButton(R.string.home_delete_category) { _, _ -> viewModel.deleteSelectedCategory() }
            .setNegativeButton(R.string.common_cancel, null)
            .show()
    }
}