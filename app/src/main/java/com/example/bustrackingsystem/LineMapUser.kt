package com.example.bustrackingsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bustrackingsystem.databinding.ActivityLineMapUserBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class LineMapUser : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RouteAdapter
    private lateinit var busDatabase:CollectionReference
    private lateinit var myRef:FirebaseFirestore
    private lateinit var binding: ActivityLineMapUserBinding
    private lateinit var list:ArrayList<String>
    private lateinit var routeList: ArrayList<RouteDetails>
    private lateinit var currentStation:String
    private lateinit var busName:String
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 1000 * 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLineMapUserBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        recyclerView=binding.ListLineMap
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        currentStation=""
        list= arrayListOf()
        routeList= arrayListOf()
        adapter= RouteAdapter(routeList)
        recyclerView.adapter=adapter
        myRef=FirebaseFirestore.getInstance()
        busDatabase=myRef.collection("BusDetails")
        busName=intent.getStringExtra("busName").toString()
        binding.TextBusName.text=busName
        busDatabase.document(busName).get()
            .addOnSuccessListener {task ->
                list.clear()

                    list.addAll(task.get("breaksName") as Collection<String>)
                currentStation=task.get("currentLocation").toString()
                for (i in list){
                    routeList.add(RouteDetails(i,currentStation.capitalize()))
                }
                adapter.notifyDataSetChanged()



                Log.d("1",task.get("breaksName").toString())


            }


        resume()
        binding.floatUpdate.setOnClickListener {
            reLoad()

        }
    }
    private fun reLoad(){
        busDatabase.document(busName).get()
            .addOnSuccessListener { task ->
                routeList.clear()

//                    Toast.makeText(this,"true,,${busName},${task.get("currentLocation")}",Toast.LENGTH_SHORT).show()
                currentStation = task.get("currentLocation").toString()
                for (i in list) {
                    routeList.add(RouteDetails(i, currentStation.capitalize()))
                }
                adapter.notifyDataSetChanged()
            }
    }
    fun resume() {

        handler.postDelayed(Runnable {

            handler.postDelayed(runnable!!, delay.toLong())
            reLoad()
//            Toast.makeText(this, "This method will run every 10 seconds", Toast.LENGTH_SHORT).show()
        }.also { runnable = it }, delay.toLong())


    }

    fun pause() {
        if (runnable!=null)
            handler.removeCallbacks(runnable!!)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        pause()
    }

}