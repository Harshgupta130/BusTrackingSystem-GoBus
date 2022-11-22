package com.example.bustrackingsystem

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView
import kotlin.coroutines.coroutineContext

class RouteAdapter(private val routeList:ArrayList<RouteDetails>):RecyclerView.Adapter<RouteAdapter.MyViewHolder>() {

    private lateinit var stopPoint:String
    private lateinit var startPoint:String
    override fun getItemCount(): Int {
        return routeList.size
    }


    override fun onBindViewHolder(holder: RouteAdapter.MyViewHolder, position: Int) {
        val route: RouteDetails = routeList[position]
        holder.StationName.text=route.stationName.toString()
        Log.d("1","${startPoint},${stopPoint}")
        if (route.stationName==startPoint) {
            Log.d("3","${route.toString()},${startPoint}")
            holder.box.setColorFilter(R.color.black)
        }
        else if(route.stationName==stopPoint){
           holder.line.visibility=View.GONE
            holder.box.setColorFilter(Color.RED)
        }

        else{
            holder.line.visibility=View.VISIBLE
            holder.box.setColorFilter(Color.parseColor("#21DC28"))
        }

        if (route.stationName==route.currentStation.toString()){
            holder.bus_icon.visibility=View.VISIBLE
            holder.box.visibility=View.GONE
            Log.d(route.stationName,route.currentStation.toString())
        }else{
            holder.bus_icon.visibility=View.GONE
            holder.box.visibility=View.VISIBLE
        }




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_line_map, parent, false)
         startPoint=routeList.first().stationName
         stopPoint=routeList.last().stationName
        return RouteAdapter.MyViewHolder(itemView)
    }
    public class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val StationName:TextView=itemView.findViewById(R.id.Text_stationName)
        val line=itemView.findViewById<View>(R.id.line)
        val box=itemView.findViewById<ImageView>(R.id.box)
        val bus_icon=itemView.findViewById<ImageView>(R.id.bus_icon)



    }
}