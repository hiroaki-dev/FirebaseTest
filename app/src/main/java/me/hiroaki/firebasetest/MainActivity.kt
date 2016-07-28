package me.hiroaki.firebasetest

import android.content.Context
import android.content.Intent
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
        fun startIntent(context: Context) = Intent(context, MainActivity::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessions.adapter = setupSpinner()

        val database = FirebaseDatabase.getInstance()
//        true: そのユーザ以外からの編集を許可しない
//        database.setPersistenceEnabled(true)

        val databaseReference = database.reference
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

}