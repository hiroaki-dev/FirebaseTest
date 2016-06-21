package me.hiroaki.firebasetest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import me.hiroaki.firebasetest.model.PushObject
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()
            signIn(email, password)
        }

        logout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "ログアウトしました", Toast.LENGTH_SHORT).show()
        }

        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser;
            if (user != null) {
                status.text = "ログイン中"
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid);
            } else {
                status.text = "未ログイン"
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        }

        sessions.adapter = setupSpinner()



        val database = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)

        val databaseReference = database.getReference()
        databaseReference.keepSynced(true)

        submit.setOnClickListener {
            val session = sessions.selectedItem
            val user = user.text
            val message = message.text
            val date = Date(System.currentTimeMillis())
            textView.text = "button clicked"

            databaseReference.child("sessions/$session/$user").setValue(PushObject(date.toString(), message.toString()),
                    DatabaseReference.CompletionListener { error, reference ->
                        if (error == null) {
                            textView.text = "onComplete"
                        } else {
                            textView.text = error.message
                        }
                    })
        }
    }

    fun setupSpinner(): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.add("session01");
        adapter.add("session02");
        adapter.add("session03");
        return adapter
    }

    fun signUp(email:String, password:String) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    Log.d(TAG, "createUserWithEmailAndPassword:onComplete:" + task.isSuccessful);

                    if (!task.isSuccessful) {
                        Toast.makeText(applicationContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
    }

    fun signIn(email:String, password:String) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "ログイン！",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "signInWithEmail", task.exception);
                        Toast.makeText(applicationContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
    }
}