package com.example.chatapplication.activity


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.onesignal.OneSignal




class MainActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var DbRef: DatabaseReference
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        DbRef= FirebaseDatabase.getInstance().getReference()
        supportActionBar?.hide()
        auth= Firebase.auth

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater =menuInflater
        menuInflater.inflate(R.menu.bottom_nav,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.quit){
            auth.signOut()
            val intent = Intent(this@MainActivity,LogInActivity::class.java)
            startActivity(intent)
            finish()

        }

        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        super.onResume()
        val currentId =FirebaseAuth.getInstance().uid
        DbRef.child("presence").child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId =FirebaseAuth.getInstance().uid
        DbRef.child("presence").child(currentId!!).setValue("Offline")
    }

    override fun onDestroy() {
        super.onDestroy()
        val currentId =FirebaseAuth.getInstance().uid
        DbRef.child("presence").child(currentId!!).setValue("Offline")
    }

    override fun onStop() {
        super.onStop()
        val currentId =FirebaseAuth.getInstance().uid
        DbRef.child("presence").child(currentId!!).setValue("Offline")
    }
}
