package com.appdemo.androidmaps.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appdemo.androidmaps.databinding.SearchResultItemBinding
import com.appdemo.androidmaps.models.PlaceNote

class SearchResultAdapter(val listener: (Double, Double) -> Unit) :
    ListAdapter<PlaceNote, SearchResultAdapter.SearchResultViewHolder>(SearchResultDiffUtils) {

    inner class SearchResultViewHolder(private val view: SearchResultItemBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(note: PlaceNote) {
            view.userName.text = note.userName.plus(": ")
            view.note.text = note.note
            view.root.setOnClickListener {
                listener.invoke(note.lat, note.long)
            }
        }
    }

    object SearchResultDiffUtils : DiffUtil.ItemCallback<PlaceNote>() {

        override fun areItemsTheSame(oldItem: PlaceNote, newItem: PlaceNote): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PlaceNote, newItem: PlaceNote): Boolean {
            return oldItem.lat == newItem.lat && oldItem.long == newItem.long
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            SearchResultItemBinding.inflate(layoutInflater, parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}