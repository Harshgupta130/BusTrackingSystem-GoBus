package com.example.bustrackingsystem

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.AndroidRuntimeException
import com.google.android.gms.location.LocationServices
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bustrackingsystem.databinding.ActivityDriverMainPageBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Error
import java.lang.Exception
import java.security.Timestamp
import java.text.SimpleDateFormat
import java.util.*

 class DriverMainPage : AppCompatActivity() {
    private lateinit var binding: ActivityDriverMainPageBinding
    private lateinit var busDatabse: CollectionReference
    private lateinit var driverDatabase: CollectionReference
    private lateinit var myRef: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 1000 * 60
    private lateinit var stationName: MutableList<String>
    private lateinit var locationName: String
    private val LOCATION_PERMISSION_REQ_CODE = 1000;
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverMainPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val bundle: Bundle? = intent.extras
        val emailid = bundle?.get("email").toString()
        val name = bundle?.get("name").toString()
        Log.d("bundel", "${emailid},${name},")
        myRef = FirebaseFirestore.getInstance()
        stationName = mutableListOf()
        busDatabse = myRef.collection("BusDetails")
        driverDatabase = myRef.collection("Driver")
        auth = FirebaseAuth.getInstance()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationName = ""
        binding.BtnSignOut.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                auth.signOut()

            }
            pause()
            intent = Intent(this, Login::class.java)
            intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding.BtnStartRide.setOnClickListener {
            busDatabse.document(binding.EditBusName.text.toString().trim { it <= ' ' })
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document.exists()) {
                            busDatabse.document(binding.EditBusName.text.toString()).get()
                                .addOnSuccessListener { stat ->
                                    stationName.clear()
                                    stationName.addAll(stat.get("breaksName") as Collection<String>)


                                }

                            if (document.getBoolean("riding") == false) {
                                binding.cardViewAfterStart.visibility = View.VISIBLE
                                binding.cardViewStart.visibility = View.GONE
                                busDatabse.document(
                                    binding.EditBusName.text.toString().trim { it <= ' ' })
                                    .update("riding", "true".toBoolean())
                                val startIntent = Intent(this, ForegroundService::class.java)
                                startIntent.putExtra("riding", "You are riding a bus ")

                                driverDatabase.document(name)
                                    .update("busNo", binding.EditBusName.text.toString())
                                busDatabse.document(binding.EditBusName.text.toString())
                                    .update(
                                        "driverName", name
                                    )

                                getCurrentLocation()
                                startIntent.putExtra("InputExtra", locationName)
                                ContextCompat.startForegroundService(this, startIntent)
                                resume()
                            } else if (document.getString("driverName") == intent.getStringExtra("name")) {

                                binding.cardViewAfterStart.visibility = View.VISIBLE
                                binding.cardViewStart.visibility = View.GONE

                                val startIntent = Intent(this, ForegroundService::class.java)
                                getCurrentLocation()
                                startIntent.putExtra("InputExtra", locationName)
                                startIntent.putExtra("riding", "You are riding a bus ")
                                ContextCompat.startForegroundService(this, startIntent)
                                resume()


                            } else {
                                Toast.makeText(
                                    this,
                                    "This bus is currently travelling.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(this, "Bus details does not exist.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "" + task.exception!!.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }


        }

        binding.BtnStopRide.setOnClickListener {
            pause()
            binding.cardViewAfterStart.visibility = View.GONE
            binding.cardViewStart.visibility = View.VISIBLE

            busDatabse.document(binding.EditBusName.text.toString().trim { it <= ' ' }).update(
                "currentLocation",
                "",
                "currentGeoLoc.Latitude",
                0,
                "currentGeoLoc.Longitude",
                0,
                "riding",
                "false".toBoolean(),
                "startTime.Time",
                "Not started", "driverName", ""
            )
            driverDatabase.document(intent.getStringExtra("name").toString()).update(
                "busNo", ""
            )
            handler.removeCallbacks(runnable!!)
            val stopIntent = Intent(this, ForegroundService::class.java)
            stopService(stopIntent)




        }
    }

    fun resume() {
        pause()
        handler.postDelayed(Runnable {

            handler.postDelayed(runnable!!, delay.toLong())
            getCurrentLocation()
//            Toast.makeText(this, "This method will run every 10 seconds", Toast.LENGTH_SHORT).show()
        }.also { runnable = it }, delay.toLong())


    }

    fun pause() {
        if (runnable!=null)
        handler.removeCallbacks(runnable!!)
    }

    private fun getCurrentLocation() {
        //Checking Location Permission

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQ_CODE
            );
            return
        }
        try {


            GlobalScope.launch(Dispatchers.Default) {
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            if (location == null) {
                                throw Exception("didnt got last location")
                            } else {
                                val geocoder = Geocoder(this@DriverMainPage, Locale.getDefault())
                                val list: List<Address> =
                                    geocoder.getFromLocation(
                                        location.latitude,
                                        location.longitude,
                                        1
                                    )
                                if (list.size == 0) {
                                    throw Exception("list Empty")
                                }

                                latitude = list[0].latitude
                                longitude = list[0].longitude
                                var addressString = mutableListOf<String>()
                                addressString.add(list[0].locality)
                                addressString.add(list[0].featureName)



                                binding.TextCurrentLocation.text = list[0].locality
                                 locationName = list[0].locality

                                Log.d(
                                    "address",
                                    "${addressString}, ${list[0].featureName}, ${list[0].locality} ${
                                        list[0].getAddressLine(0)
                                    }"
                                )
                                busDatabse.document(binding.EditBusName.text.toString())
                                    .update(
                                        "currentGeoLoc.Latitude", latitude,
                                        "currentGeoLoc.Longitude", longitude,

                                        "startTime", Calendar.getInstance().time
                                    )
                                Log.d("abcd", stationName.toString())
                                var check: String? = checkFun(addressString, stationName)
                                if (check != null) {
                                    busDatabse.document(binding.EditBusName.text.toString())
                                        .update("currentLocation", check)
                                }

                            }
                        }


                    }
                    .addOnFailureListener {

//                Toast.makeText(this@launch,"Failed on Getting Current Location",Toast.LENGTH_SHORT).show()

                    }
            }
        } catch (e: Exception) {
            Log.d("error", e.toString())
        } catch (e: AndroidRuntimeException) {
            Log.d("runtime", e.toString())
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQ_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    //permission granted
                } else {
                    //Permission denied

                    Toast.makeText(
                        this, "You Need To Grant " +
                                "Permission to access location", Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    private fun checkFun(address: MutableList<String>, stationName: MutableList<String>): String? {

        for (i in address) {
            if (i.capitalize() in stationName) {
                return i
            }
        }

        return null
    }


}