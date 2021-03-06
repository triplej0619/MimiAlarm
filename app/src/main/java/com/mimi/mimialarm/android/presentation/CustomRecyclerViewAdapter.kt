package com.mimi.mimialarm.android.presentation

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

abstract class CustomRecyclerViewAdapter<T> : RecyclerView.Adapter<CustomRecyclerViewHolder> {
    protected val layoutId: Int
    protected var items: List<T>? = null
    protected var itemClick: IListItemClick? = null
    protected var longClick: IListItemClick? = null

    constructor(items: List<T>?, layoutId: Int) {
        this.items = items
        this.layoutId = layoutId
    }

    constructor(items: List<T>?, layoutId: Int, itemClick: IListItemClick) {
        this.items = items
        this.layoutId = layoutId
        this.itemClick = itemClick
    }

    constructor(items: List<T>?, layoutId: Int, itemClick: IListItemClick, longClick: IListItemClick) {
        this.items = items
        this.layoutId = layoutId
        this.itemClick = itemClick
        this.longClick = longClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomRecyclerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

        return CustomRecyclerViewHolder(itemView, itemClick, longClick)
    }

    override fun onBindViewHolder(holder: CustomRecyclerViewHolder, position: Int) {
        val item = items!![position]
        setViewModel(holder, item)
    }

    protected abstract fun setViewModel(holder: CustomRecyclerViewHolder, item: T)

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    fun addItems() {
        notifyDataSetChanged()
    }

    fun addItem(position: Int) {
        notifyItemInserted(position)
    }

    fun clear() {
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        notifyItemRemoved(position)
    }
}

