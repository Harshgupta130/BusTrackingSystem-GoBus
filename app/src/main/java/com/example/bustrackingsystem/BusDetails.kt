package com.example.bustrackingsystem

import com.google.firebase.firestore.GeoPoint
import java.security.Timestamp

data class BusDetails(var busName:String?=null, var startPoint:String?=null, var stopPoint:String?=null){

}
