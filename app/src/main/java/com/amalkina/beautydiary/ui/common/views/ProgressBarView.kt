package com.amalkina.beautydiary.ui.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat.getColor
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.databinding.ViewProgressBarBinding
import kotlin.math.min

class ProgressBarView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val binding = ViewProgressBarBinding.inflate(LayoutInflater.from(context))
    private var progress: Int = MIN_PROGRESS

    init {
        addView(binding.root)
        obtainAttrs(attrs)
        updateView()
    }

    fun setProgress(value: Int) {
        progress = value
        updateView()
    }

    private fun obtainAttrs(attrs: AttributeSet?) {
        attrs?.let {
            val typedAttrs = context.obtainStyledAttributes(attrs, R.styleable.ProgressBarView)
            typedAttrs.apply {
                progress = min(
                    getInteger(R.styleable.ProgressBarView_progress, MIN_PROGRESS),
                    MAX_PROGRESS
                )
            }
            typedAttrs.recycle()
        }
    }

    private fun updateView() {
        binding.progress = progress
        binding.progressTint = getProgressBarColor()
    }

    private fun getProgressBarColor(): Int {
        return when {
            progress < 50 -> getColor(context, R.color.progress_color_min)
            progress < 65 -> getColor(context, R.color.progress_color_medium)
            progress < 80 -> getColor(context, R.color.progress_color_normal)
            else -> getColor(context, R.color.progress_color_max)
        }
    }

    companion object {
        const val MIN_PROGRESS = 1
        const val MAX_PROGRESS = 100
    }
}