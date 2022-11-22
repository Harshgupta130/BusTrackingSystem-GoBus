package com.example.bustrackingsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bustrackingsystem.databinding.ActivityUserMainPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import java.security.Timestamp




class UserMainPage : AppCompatActivity() {
    private lateinit var binding: ActivityUserMainPageBinding
    private lateinit var busDatabase: CollectionReference
    private lateinit var userDatabase: CollectionReference
    private lateinit var myRef: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BusAdapter
    private lateinit var arrayList:ArrayList<BusDetails>
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMainPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        var intent:Intent
        recyclerView=binding.recyclerView
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        arrayList= arrayListOf()
        adapter=BusAdapter(arrayList)
        recyclerView.adapter=adapter
        auth=FirebaseAuth.getInstance()
        myRef = FirebaseFirestore.getInstance()
        busDatabase = myRef.collection("BusDetails")
        userDatabase = myRef.collection("User")
        showAll()
        binding.BtnSearch.setOnClickListener {
            val startPoint=binding.EditStarPoint.text.toString().capitalize()
            val destination=binding.EditDestination.text.toString().capitalize()
            busDatabase
                .whereArrayContains("breaksName",startPoint)
                .get()
                .addOnSuccessListener{ task ->
                    arrayList.clear()
                            for(document in task){

                                val list= mutableListOf<String>()
                                list.addAll(document.get("breaksName") as Collection<String>)
                                if (destination in list &&(list.indexOf(startPoint)<list.indexOf(destination))){
                                Log.d("abcd","${task.size()},${list}")
                                arrayList.add(BusDetails(document.get("busName").toString(),document.get("startPoint").toString(),document.get("stopPoint").toString()))
                                    adapter.notifyDataSetChanged()}





                    }


                }
                .addOnFailureListener {
                    Log.d("fail","error",it)
                }


        }
        adapter.setOnItemClickListener(object:BusAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(this@UserMainPage,"${arrayList[position].busName.toString()}",Toast.LENGTH_SHORT).show()

                lineMap(arrayList[position].busName.toString())
            }

        })
        binding.BtnSearch.setOnLongClickListener {
            showAll()
        true}
        binding.BtnSignOut.setOnClickListener {
            val user=auth.currentUser
            if (user!=null){
                auth.signOut()

            }
             intent=Intent(this,Login::class.java)
            intent?.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

    private fun lineMap(busName:String) {
        intent=Intent(this,LineMapUser::class.java)
        intent.putExtra("busName",busName)
        startActivity(intent)
    }
    private fun showAll(){
        arrayList.clear()
        busDatabase.get()
            .addOnSuccessListener { task->
                for (document in task){
                    arrayList.add(BusDetails(document.get("busName").toString(),document.get("startPoint").toString(),document.get("stopPoint").toString()))
                    adapter.notifyDataSetChanged()
                }

            }
    }


}


