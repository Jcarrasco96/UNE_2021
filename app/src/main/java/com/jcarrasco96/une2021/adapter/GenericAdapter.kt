package com.jcarrasco96.une2021.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView

abstract class GenericAdapter<T, AH : RecyclerView.ViewHolder> : RecyclerView.Adapter<AH>() {

    var listItems = ArrayList<T>()
    lateinit var context: Context

    override fun onBindViewHolder(holder: AH, position: Int) {
        bind(holder, listItems[position])
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    fun updateList(newList: ArrayList<T>, oldCount: Int, showListSize: Int) {
        this.listItems = newList
        notifyItemRangeChanged(oldCount, showListSize)
    }

    fun add(element: T) {
        this.listItems.add(0, element)
        notifyItemInserted(0)
    }

    abstract fun bind(holder: AH, data: T)

}
