package com.mimi.mimialarm.android.presentation

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Created by MihyeLee on 2017. 5. 24..
 */

abstract class CustomRecyclerViewAdapter<T> : RecyclerView.Adapter<CustomRecyclerViewHolder> {
    private val layoutId: Int
    private var items: List<T>? = null
    private var itemClick: IListItemClick? = null

    constructor(items: List<T>?, layoutId: Int) {
        this.items = items
        this.layoutId = layoutId
    }

    constructor(items: List<T>?, layoutId: Int, itemClick: IListItemClick) {
        this.items = items
        this.layoutId = layoutId
        this.itemClick = itemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomRecyclerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

        return CustomRecyclerViewHolder(itemView, itemClick)
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

