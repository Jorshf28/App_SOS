package com.example.app_sos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StatusAdapter(private val statusList: List<StatusItem>) : RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_status, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val statusItem = statusList[position]
        holder.bind(statusItem)
    }

    override fun getItemCount(): Int = statusList.size

    class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tvDescription)

        fun bind(statusItem: StatusItem) {
            titleTextView.text = statusItem.title
            descriptionTextView.text = statusItem.description
        }
    }
}

data class StatusItem(val title: String, val description: String)