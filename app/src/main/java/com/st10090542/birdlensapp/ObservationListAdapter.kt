package com.st10090542.birdlensapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ObservationListAdapter(private val observations: List<BirdObservation>):
    RecyclerView.Adapter<ObservationListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val speciesTextView: TextView = itemView.findViewById(R.id.speciesTextView)
        val notesTextView: TextView = itemView.findViewById(R.id.notesTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.observation_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val observation = observations[position]
        holder.speciesTextView.text = observation.species
        holder.notesTextView.text = observation.notes
        holder.locationTextView.text = "Location: ${observation.latitude}, ${observation.longitude}"
        holder.dateTextView.text = "Date: ${observation.date.toString()}"
    }

    override fun getItemCount(): Int {
        return observations.size
    }
}
