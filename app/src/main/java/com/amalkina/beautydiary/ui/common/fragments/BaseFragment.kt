package com.amalkina.beautydiary.ui.common.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.ui.common.ext.hasPermission
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class BaseFragment : Fragment() {

    companion object {
        var dialog: AlertDialog? = null
    }

    private var lastEventTimestamp = 0L

    // todo: надо получить контекст из активити по-хорошему
    private var _context: Context? = null
    private var permission: String? = null
    private var permissionDeniedMessage: String? = null
    private var onPermissionGranted: (() -> Unit)? = null

    fun updateEventTimestamp(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastEventTimestamp < 500) {
            return false
        }
        lastEventTimestamp = now
        return true
    }

    fun showInfoDialog(context: Context, @StringRes message: Int) {
        showInfoDialog(context, getString(message))
    }

    fun showInfoDialog(context: Context, message: String) {
        dialog?.dismiss()
        dialog = MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .setPositiveButton(R.string.common_ok, null)
            .show()
    }

    fun showErrorDialog(context: Context, message: String) {
        dialog?.dismiss()
        dialog = MaterialAlertDialogBuilder(context)
            .setTitle(R.string.common_error)
            .setMessage(message)
            .setPositiveButton(R.string.common_ok, null)
            .show()
    }

    fun withPermission(context: Context, permission: String, message: String, onSucceed: () -> Unit) {
        // TODO: некрасиво как-то
        _context = context
        this.permission = permission
        permissionDeniedMessage = message
        onPermissionGranted = onSucceed

        when {
            context.hasPermission(permission) -> {
                dialog?.dismiss()
                onSucceed.invoke()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                handleShouldShowRequestPermissionRationale(context, message)
            }
            else -> requestPermissionLauncher.launch(permission)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun handleShouldShowRequestPermissionRationale(context: Context, message: String) {
        if (settingsIntent(context).resolveActivity(context.packageManager) != null) {
            showSettingsDialog(context, message)
        } else {
            showInfoDialog(context, message)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            dialog?.dismiss()
            onPermissionGranted?.invoke()
        } else {
            _context?.let { context ->
                permissionDeniedMessage?.let { handleShouldShowRequestPermissionRationale(context, it) }
            }
        }
    }

    private fun settingsIntent(context: Context): Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        val uri = Uri.fromParts("package", context.packageName, null)
        this.data = uri
    }

    private fun showSettingsDialog(context: Context, message: String) {
        MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .setNegativeButton(R.string.common_cancel, null)
            .setPositiveButton(R.string.common_settings) { _, _ ->
                settingsLauncher.launch(settingsIntent(context))
            }
            .show()
    }

    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        dialog?.dismiss()
        permission?.let { perm ->
            if (_context?.hasPermission(perm) == true)
                onPermissionGranted?.invoke()
        }
        return@registerForActivityResult
    }

}