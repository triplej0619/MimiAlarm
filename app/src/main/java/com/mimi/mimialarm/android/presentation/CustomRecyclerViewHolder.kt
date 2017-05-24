package com.mimi.mimialarm.android.presentation

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class CustomRecyclerViewHolder(itemView: View, itemClick: IListItemClick?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val binding: ViewDataBinding
    var itemClick: IListItemClick? = null

    init {
        binding = DataBindingUtil.bind<ViewDataBinding>(itemView)
        this.itemClick = itemClick
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (itemClick != null) {
            val pos = layoutPosition
            itemClick!!.clickEvent(v, pos)
        }
    }
}