package com.example.balldontlie.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.balldontlie.R
import com.example.balldontlie.model.Schedule

class ScheduleAdapter(private val scheduleData: List<Schedule>) :
    RecyclerView.Adapter<ScheduleAdapter.CardViewHolder>() {
    // Replace test data with cards and such

    class CardViewHolder(private val cardView : CardView) : RecyclerView.ViewHolder(cardView) {
        val score = cardView.findViewById<TextView>(R.id.scoreText)
        val team = cardView.findViewById<TextView>(R.id.teamText)
        val stadium = cardView.findViewById<TextView>(R.id.stadiumText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        // TODO - Ben said something about binding listeners here with adapterPosition
        val cardView = LayoutInflater.from(
            parent.context).inflate(R.layout.past_schedule_card, parent, false) as CardView
        return CardViewHolder(
            cardView
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.score.text = scheduleData[position].score
        holder.team.text = scheduleData[position].team
        holder.stadium.text = scheduleData[position].stadium
    }

    override fun getItemCount(): Int {
        return scheduleData.size
    }
}