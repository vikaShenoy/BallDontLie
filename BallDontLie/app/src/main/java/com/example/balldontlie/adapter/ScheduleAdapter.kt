package com.example.balldontlie.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.balldontlie.R
import com.example.balldontlie.model.Schedule

/**
 * Manage the schedule fragment's schedule cards.
 */
class ScheduleAdapter(private val scheduleData: List<Schedule>, ctx: Context) :
    RecyclerView.Adapter<ScheduleAdapter.CardViewHolder>() {
    val ctx: Context = ctx

    class CardViewHolder(private val cardView: CardView) : RecyclerView.ViewHolder(cardView) {
        val score: TextView = cardView.findViewById<TextView>(R.id.scoreText)
        val team: TextView = cardView.findViewById<TextView>(R.id.teamText)
        val stadium: TextView = cardView.findViewById<TextView>(R.id.stadiumText)
        val date: TextView = cardView.findViewById<TextView>(R.id.dateText)
        val card: CardView = cardView.findViewById<CardView>(R.id.mainScheduleCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val cardView = LayoutInflater.from(
            parent.context
        ).inflate(R.layout.schedule_card, parent, false) as CardView
        return CardViewHolder(
            cardView
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.score.text = scheduleData[position].score
        holder.team.text = scheduleData[position].team
        holder.stadium.text = scheduleData[position].stadium
        holder.date.text = scheduleData[position].date

        if (scheduleData[position].win) {
            holder.card.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimaryDark))
        } else {
            holder.card.setCardBackgroundColor(
                ContextCompat.getColor(
                    ctx,
                    R.color.colorPrimary
                )
            )
        }

    }

    override fun getItemCount(): Int {
        return scheduleData.size
    }
}