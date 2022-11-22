package com.example.bustrackingsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bustrackingsystem.databinding.ActivityEntryPageBinding

class EntryPage : AppCompatActivity() {
    private lateinit var binding:ActivityEntryPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEntryPageBinding.inflate(layoutInflater)
        val view =binding.root
        setContentView(view)

        var intent:Intent
        binding.BtnDriverReg.setOnClickListener {
            intent=Intent(this,DriverRegistration::class.java)
            startActivity(intent)
        }
        binding.BtnUserReg.setOnClickListener {
            intent=Intent(this,UserRegistration::class.java)
            startActivity(intent)
        }
        binding.AlreadyAccount.setOnClickListener{
            intent=Intent(this,Login  ::class.java)
            startActivity(intent)
        }
    }
}