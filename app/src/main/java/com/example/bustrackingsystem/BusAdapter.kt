package com.example.bustrackingsystem

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView


class BusAdapter(
    private val busList: ArrayList<BusDetails>
) : RecyclerView.Adapter<BusAdapter.MyViewHolder>() {
    private lateinit var mListener:onItemClickListener
interface onItemClickListener{
    fun onItemClick(position:Int)
}
    fun setOnItemClickListener(listener:onItemClickListener) {
        mListener=listener
    }

    override fun getItemCount(): Int {
        return busList.size
    }

    override fun onBindViewHolder(holder: BusAdapter.MyViewHolder, position: Int) {
        val bus: BusDetails = busList[position]
        holder.busName.text = bus.busName
        holder.startPoint.text = bus.startPoint
        holder.destination.text = bus.stopPoint

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.bus_list, parent, false)

        return MyViewHolder(itemView,mListener)
    }

    public class MyViewHolder(itemView: View,listener:onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val busName: TextView = itemView.findViewById(R.id.Text_busName)
        val startPoint: TextView = itemView.findViewById(R.id.Text_startPoint)
        val destination: TextView = itemView.findViewById(R.id.Text_destination)
//         val scheduleTime:TextView=itemView.findViewById(R.id.Text_scheduleTime)
//         val lateBy:TextView=itemView.findViewById(R.id.Text_lateBy)
//         val expectedTime:TextView=itemView.findViewById(R.id.Text_expectedTime)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}


