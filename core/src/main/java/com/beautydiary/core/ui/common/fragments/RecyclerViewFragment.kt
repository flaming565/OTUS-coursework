package com.beautydiary.core.ui.common.fragments

import android.os.Parcelable
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.beautydiary.core.R
import com.github.akvast.mvvm.adapter.ViewModelAdapter

abstract class RecyclerViewFragment : BaseFragment() {

    protected var rvAdapter: ViewModelAdapter? = null
    private var rvState: Parcelable? = null

    abstract fun getRecyclerView(): RecyclerView?

    override fun onResume() {
        super.onResume()
        getRecyclerView()?.layoutManager?.onRestoreInstanceState(rvState)
    }

    override fun onPause() {
        super.onPause()
        rvState = getRecyclerView()?.layoutManager?.onSaveInstanceState()
    }

    protected fun startListAnimation(minSize: Int = 2) {
        getRecyclerView()?.let {
            val itemsSize = rvAdapter?.items?.size ?: -1
            if (itemsSize < minSize)
                it.layoutAnimation = null
            else if (it.layoutAnimation == null) {
                it.layoutAnimation =
                    AnimationUtils.loadLayoutAnimation(
                        requireContext(),
                        R.anim.anim_recycler_view
                    )
                it.layoutAnimation.start()
            }
        }
    }

}