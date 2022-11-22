package com.example.bustrackingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bustrackingsystem.databinding.ActivityDriverRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern


class DriverRegistration : AppCompatActivity() {
    private lateinit var binding: ActivityDriverRegistrationBinding
    var isAllFieldsChecked = false
    private lateinit var myRef:FirebaseFirestore
    private lateinit var driverDetails: DriverDetails
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDriverRegistrationBinding.inflate(layoutInflater)
        val view =binding.root
        setContentView(view)
        myRef= FirebaseFirestore.getInstance()
        var intent:Intent
        binding.AlreadyAccount.setOnClickListener{
            intent=Intent(this,Login::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        binding.BtnRegister.setOnClickListener {
            isAllFieldsChecked=checkAllFields()

            if(isAllFieldsChecked){
                auth=FirebaseAuth.getInstance()

                auth.createUserWithEmailAndPassword(binding.EditDriverEmail.text.toString().trim{it <=' '}
                    ,binding.EditDriverPassword.text.toString().trim{it<=' '})
                    .addOnFailureListener {
                        Toast.makeText(this,"failed to register on authentication server",Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                            task->
                        if(task.isSuccessful){
                            firebaseUser =task.result!!.user!!


                            val doc= myRef.collection("Driver").document(binding.EditDriverName.text.toString())
                            driverDetails= DriverDetails(binding.EditDriverName.text.toString(),
                                binding.EditDriverEmail.text.toString(),firebaseUser.uid,
                                binding.EditDriverBusNo.text.toString())

                            doc.set(driverDetails)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Unseccessfull", Toast.LENGTH_SHORT).show()

                                }

                             intent=Intent(this,Login::class.java)
                            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this,task.exception!!.message.toString().toString(),
                                Toast.LENGTH_SHORT).show()
                        }

                    }
            }




        }
    }

    private fun checkAllFields():Boolean{
        if (binding.EditDriverName.text.toString().trim().equals("")){
            binding.EditDriverName.setError("Driver name is required")
            return false
        }
        if (binding.EditDriverEmail.text.toString().trim().equals("")){
            binding.EditDriverEmail.setError("Driver email is required")
            return false
        }
        if (binding.EditDriverPassword.text.toString().trim().equals("")){
            binding.EditDriverPassword.setError("Driver password is required")
            return false
        }
        val letter = Pattern.compile("[a-zA-z]")
        val digit = Pattern.compile("[0-9]")
        val special = Pattern.compile("[@#$%&*_?]")
        //Pattern eight = Pattern.compile (".{8}");
        val password=binding.EditDriverPassword.text.toString()


        //Pattern eight = Pattern.compile (".{8}");
        val hasLetter = letter.matcher(password)
        val hasDigit = digit.matcher(password)
        val hasSpecial = special.matcher(password)
        if(binding.EditDriverPassword.text.toString().length<8 ||
            !hasLetter.find() || !hasDigit.find() || !hasSpecial.find() ){
            binding.EditDriverPassword.setError("Password length>=8 a-z,A-Z,0-9,?,#,@,%,*,_,$,&")
            return false
        }

        return true
    }
}
class DriverDetails {
    var name: String = ""
    var email: String = ""
    var driverId: String = ""
    var busNo: String = ""

    constructor(name: String, email: String, driverId: String, busNo: String) {
        this.name = name
        this.email = email
        this.driverId = driverId
        this.busNo = busNo
    }
}