package com.mimi.mimialarm.android.presentation

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

class CustomRecyclerViewHolder(itemView: View, itemClick: IListItemClick?, longClick: IListItemClick?)
    : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

    val binding: ViewDataBinding
    var itemClick: IListItemClick? = null
    var longClick: IListItemClick? = null

    init {
        binding = DataBindingUtil.bind<ViewDataBinding>(itemView)
        this.itemClick = itemClick
        this.longClick = longClick

        if(this.itemClick != null) {
            itemView.setOnClickListener(this)
        }

        if(this.longClick != null) {
            itemView.setOnLongClickListener(this)
        }
    }

    override fun onClick(v: View) {
        val pos = layoutPosition
        itemClick?.clickEvent(v, pos)
    }

    override fun onLongClick(v: View?): Boolean {
        longClick?.clickEvent(v!!, 0)
        return true
    }
}