package com.beautydiary.home.fragments

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.beautydiary.core_ui.fragments.BaseFragment
import com.beautydiary.home.R
import com.beautydiary.home.databinding.DialogAddCategorySelectImageBinding
import com.beautydiary.home.databinding.FragmentAddCategoryBinding
import com.beautydiary.home.vm.AddCategoryViewModel
import com.beautydiary.home.vm.AddCategoryViewModel.LaunchItem
import com.beautydiary.home.vm.AddCategoryViewModel.UserAction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.weigan.loopview.LoopView
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class AddCategoryFragment : BaseFragment() {

    private val args by navArgs<AddCategoryFragmentArgs>()
    private val viewModel by viewModel<AddCategoryViewModel> { parametersOf(args.categoryId) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddCategoryBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }

        viewModel.categoryNames.observe { list ->
            initLoopView(binding.loopView, list)
        }

        viewModel.readyImagePathEvent.observe { path ->
            path?.let { viewModel.updateCategoryImage(it) }
        }

        viewModel.userActionEvent.observe(viewLifecycleOwner) { event ->
            event.let { action ->
                if (!updateEventTimestamp()) return@let
                when (action) {
                    UserAction.CHANGE_IMAGE -> showSelectImageDialog()
                    UserAction.SELECT_CAMERA -> checkPermission(Manifest.permission.CAMERA)
                    UserAction.SELECT_GALLERY -> checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    UserAction.UPDATE_CATEGORY -> findNavController().popBackStack()
                }
            }
        }

        viewModel.launchActionEvent.observe(viewLifecycleOwner) { event ->
            event.let {
                when (it) {
                    LaunchItem.LAUNCH_CAMERA -> viewModel.getTempUri()?.let { uri ->
                        cameraLauncher.launch(uri)
                    }
                    LaunchItem.LAUNCH_GALLERY -> galleryLauncher.launch("image/*")
                }
            }
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            event.let { showInfoDialog(requireContext(), it) }
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
            if (viewModel.currentIndex.value < 0)
                viewModel.currentIndex.value = 0
        }
    }

    private fun showSelectImageDialog() {
        val dialogBinding = DialogAddCategorySelectImageBinding.inflate(layoutInflater).apply {
            vm = viewModel
        }
        dialog?.dismiss()
        dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .show()
    }

    private fun checkPermission(permission: String) {
        val message = when (permission) {
            Manifest.permission.CAMERA -> getString(R.string.add_category_permission_rational_camera)
            Manifest.permission.READ_EXTERNAL_STORAGE -> getString(R.string.add_category_permission_rational_gallery)
            else -> ""
        }
        withPermission(requireContext(), permission, message) {
            when (permission) {
                Manifest.permission.CAMERA -> viewModel.getImageFromCamera()
                Manifest.permission.READ_EXTERNAL_STORAGE -> viewModel.getImageFromGallery()
            }
        }
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
            viewModel.notifyReadyImageFromCamera(isSaved)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { nullableUri: Uri? ->
            nullableUri?.let { uri ->
                viewModel.notifyReadyImageFromGallery(uri)
            }
        }
}