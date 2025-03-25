package com.pahadi.uncle.presentation.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pahadi.uncle.R

class SettingsAdapter(private val onSettingsItemClickedListener: (actionId: Int) -> Unit) :
    RecyclerView.Adapter<SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SettingsViewHolder.create(onSettingsItemClickedListener, parent)

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.bind(allSettings[position])
    }

    override fun getItemCount() = allSettings.size
}

class SettingsViewHolder(
    private val onSettingsItemClickedListener: (actionId: Int) -> Unit, view: View
) : RecyclerView.ViewHolder(view) {
    private val icon: ImageView = view.findViewById(R.id.icon)
    private val title: TextView = view.findViewById(R.id.title)

    fun bind(settingsItem: SettingsItem) {
        title.text = itemView.context.resources.getString(settingsItem.titleResId)
        icon.setImageResource(settingsItem.icon)
        itemView.setOnClickListener {
            onSettingsItemClickedListener(settingsItem.directionId)
        }
    }

    companion object {
        fun create(onSettingsItemClickedListener: (actionId: Int) -> Unit, parent: ViewGroup): SettingsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_settings, parent, false)
            return SettingsViewHolder(onSettingsItemClickedListener, view)
        }
    }
}
