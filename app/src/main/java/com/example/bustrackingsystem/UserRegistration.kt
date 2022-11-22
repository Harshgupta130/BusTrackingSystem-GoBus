package com.example.bustrackingsystem

import android.R.attr.password
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bustrackingsystem.databinding.ActivityUserRegistrationBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser

import java.util.regex.Pattern


class UserRegistration : AppCompatActivity() {
    private lateinit var binding: ActivityUserRegistrationBinding
    var isAllFieldsChecked = false
    private lateinit var myRef: FirebaseFirestore
    private lateinit var userDetails: UserDetails
    private lateinit var auth: FirebaseAuth
    private lateinit var  firebaseUser :FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserRegistrationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        myRef = FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()

        var intent:Intent
        binding.AlreadyAccount.setOnClickListener{
            intent=Intent(this,Login::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        binding.BtnRegister.setOnClickListener {

            isAllFieldsChecked = checkAllFields()

            if (isAllFieldsChecked) {
                val doc = myRef.collection("User").document(binding.EditUserName.text.toString())
                auth=FirebaseAuth.getInstance()

                auth.createUserWithEmailAndPassword(binding.EditUserEmail.text.toString().trim{it <=' '}
                    ,binding.EditUserPassword.text.toString().trim{it<=' '})
                    .addOnFailureListener {
                        Toast.makeText(this,"failed to register on authentication server",Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                            task->
                        if(task.isSuccessful){
                             firebaseUser =task.result!!.user!!

                            userDetails = UserDetails(
                                binding.EditUserName.text.toString(),
                                binding.EditUserEmail.text.toString(),firebaseUser.uid
                            )


                            doc.set(userDetails)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Unseccessfull", Toast.LENGTH_SHORT).show()

                                }

                             intent = Intent(this, Login::class.java)
                            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this,task.exception!!.message.toString(),
                            Toast.LENGTH_SHORT).show()
                        }
                    }


            }
        }
    }

    private fun checkAllFields(): Boolean {

        if (binding.EditUserName.text.toString().trim().equals("")) {
            binding.EditUserName.setError("User name is required")
            return false
        }

        if (binding.EditUserEmail.text.toString().trim().equals("")) {
            binding.EditUserEmail.setError("User email is required")
            return false
        }
        if (binding.EditUserPassword.text.toString().trim().equals("")) {
            binding.EditUserPassword.setError("User password is required")
            return false
        }
        val letter = Pattern.compile("[a-zA-z]")
        val digit = Pattern.compile("[0-9]")
        val special = Pattern.compile("[@#$%&*_?]")
        //Pattern eight = Pattern.compile (".{8}");
        val password = binding.EditUserPassword.text.toString()


        //Pattern eight = Pattern.compile (".{8}");
        val hasLetter = letter.matcher(password)
        val hasDigit = digit.matcher(password)
        val hasSpecial = special.matcher(password)
        if (binding.EditUserPassword.text.toString().length < 8 ||
            !hasLetter.find() || !hasDigit.find() || !hasSpecial.find()
        ) {
            binding.EditUserPassword.setError("Password length>=8 a-z,A-Z,0-9,?,#,@,%,*,_,$,&")
            return false
        }
        return true
    }
}

class UserDetails {
    var name: String = ""
    var email: String = ""
    var userid: String = ""

    constructor(name: String, email: String, userid: String) {
        this.name = name
        this.email = email
        this.userid = userid

    }
}