package com.amalkina.beautydiary.ui.home.fragments

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amalkina.beautydiary.BR
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.databinding.DialogHomeSelectCategoryOptionsBinding
import com.amalkina.beautydiary.databinding.FragmentHomeBinding
import com.amalkina.beautydiary.ui.common.fragments.RecyclerViewFragment
import com.amalkina.beautydiary.ui.home.models.HomeCategory
import com.amalkina.beautydiary.ui.home.models.HomeCategoryNew
import com.amalkina.beautydiary.ui.home.vm.HomeViewModel
import com.amalkina.beautydiary.ui.home.vm.HomeViewModel.UserAction
import com.github.akvast.mvvm.adapter.ViewModelAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel


class HomeFragment : RecyclerViewFragment() {
    private val viewModel by viewModel<HomeViewModel>()

    private var binding: FragmentHomeBinding? = null

    override fun getRecyclerView(): RecyclerView? = binding?.recyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.to_do_list -> {
                        findNavController().navigate(HomeFragmentDirections.openTodoList())
                        true
                    }
                    R.id.statistics -> {
                        findNavController().navigate(HomeFragmentDirections.openStatistics())
                        true
                    }
                    else -> false
                }
            }
        }

        rvAdapter = makeAdapter()
        binding?.recyclerView?.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = rvAdapter
            itemAnimator = DefaultItemAnimator()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collect {
                    rvAdapter?.items = it.toTypedArray()
                    startListAnimation()
                }
            }
        }

        viewModel.userActionEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                if (!updateEventTimestamp()) return@let
                dialog?.dismiss()

                when (it) {
                    UserAction.ON_CLICK -> {
                        viewModel.selectedCategory?.let { category ->
                            findNavController().navigate(
                                HomeFragmentDirections.openTaskList(category)
                            )
                        }
                    }
                    UserAction.ON_LONG_CLICK -> showCategoryOptionsDialog()
                    UserAction.ADD_CATEGORY -> findNavController().navigate(HomeFragmentDirections.openAddCategory())
                    UserAction.EDIT_CATEGORY -> viewModel.selectedCategory?.let { category ->
                        findNavController().navigate(HomeFragmentDirections.openAddCategory(category.id))
                    }
                    UserAction.DELETE_CATEGORY -> showDeleteCategoryDialog()
                }
            }
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                // todo: error processing
            }
        }

        return binding?.root
    }

    private fun makeAdapter() = object : ViewModelAdapter(viewLifecycleOwner) {
        init {
            setHasStableIds(true)

            sharedObject(viewModel, BR.home)
            cell(HomeCategory::class.java, R.layout.cell_home_category, BR.vm)
            cell(HomeCategoryNew::class.java, R.layout.cell_home_category_new, BR.vm)
        }

        override fun getItemId(position: Int): Long {
            return when (val item = items[position]) {
                is HomeCategory -> item.id
                else -> super.getItemId(position)
            }
        }

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is HomeCategory && newItem is HomeCategory)
                return oldItem.id == newItem.id

            return super.areItemsTheSame(oldItem, newItem)
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
            .setPositiveButton(R.string.common_delete) { _, _ -> viewModel.deleteSelectedCategory() }
            .setNegativeButton(R.string.common_cancel, null)
            .show()
    }
}