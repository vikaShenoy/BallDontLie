package com.example.balldontlie

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScheduleAdapter(private val testData: Array<String>) : RecyclerView.Adapter<ScheduleAdapter.CardViewHolder>() {
    // Replace test data with cards and such

    class CardViewHolder(val textView : TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleAdapter.CardViewHolder {
        // typically it would set the view by inflating an xml file
        val textView = TextView(parent.context)
        textView.text = "test"
        return CardViewHolder(textView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.textView.text = testData[position]
    }

    override fun getItemCount(): Int {
        return testData.size
    }
}