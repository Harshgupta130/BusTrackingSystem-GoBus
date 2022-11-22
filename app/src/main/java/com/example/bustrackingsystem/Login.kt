package com.example.bustrackingsystem

import android.content.Intent
import android.nfc.Tag
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bustrackingsystem.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    var isAllFieldsChecked = false

    private lateinit var auth: FirebaseAuth
    private lateinit var myRef: FirebaseFirestore
    private lateinit var driver:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        var intent: Intent
        binding.dontHaveAccount.setOnClickListener {
            intent = Intent(this, EntryPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        binding.BtnLogin.setOnClickListener {
            isAllFieldsChecked = checkAllFields()
            driver="driver"
            if (isAllFieldsChecked) {
                auth = FirebaseAuth.getInstance()
                myRef = FirebaseFirestore.getInstance()
                val currentUser = auth.currentUser
                if(true) {
                    auth = FirebaseAuth.getInstance()
                    auth.signInWithEmailAndPassword(binding.EditLoginEmail.text.toString()
                        .trim { it <= ' ' },
                        binding.EditLoginPass.text.toString().trim { it <= ' ' })
                        .addOnCompleteListener { task ->


                            if (task.isSuccessful) {
                                val doc=task.result
                                val driverRef = myRef.collection("Driver")
                                    .whereEqualTo("email", binding.EditLoginEmail.text.toString()).count()
                                    .get(AggregateSource.SERVER).addOnCompleteListener { task2 ->
                                        if (task2.isSuccessful) {
                                            val TAG = "abs"
                                            val snapshot = task2.result
                                            Log.d(TAG, "count :${doc.user}")

                                            if (snapshot.count.toInt()==1) {




                                                myRef.collection("Driver").whereEqualTo("email",
                                                    binding.EditLoginEmail.text.toString()).get()
                                                    .addOnSuccessListener { task3->

                                                            driver=task3.documents.get(0).id.toString()
                                                            Log.d("task3",task3.documents.get(0).id.toString())
                                                            driverMainPage(binding.EditLoginEmail.text.toString(),driver)



                                                    }


                                            }
                                            else{
                                                intent = Intent(this, UserMainPage::class.java)
                                                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                startActivity(intent)
                                            }
                                        }
                                        else {
                                            Toast.makeText(this,"1: ${task2.exception?.message}",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                            else{
                                Toast.makeText(this,"2 : ${task.exception?.message}",Toast.LENGTH_SHORT).show()
                            }




//
                        }
                }
            }
        }
    }

        private fun checkAllFields(): Boolean {
            if (binding.EditLoginEmail.text.toString().trim().equals("")) {
                binding.EditLoginEmail.setError("Email is required")
                return false
            }

            if (binding.EditLoginPass.text.toString().trim().equals("")) {
                binding.EditLoginPass.setError("Password is required")
                return false
            }

            return true
        }
    private fun driverMainPage(email:String,name:String){intent.putExtra("email",binding.EditLoginEmail.text.toString())
        intent = Intent(this,DriverMainPage::class.java)
        intent.putExtra("email",email)
        intent.putExtra("name", name)
        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)


    }

    }


