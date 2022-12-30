package com.example.chatapplication.logInFragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatapplication.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso



class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fillDatas()
    }

    private fun fillDatas(){
        val reference =FirebaseDatabase.getInstance().reference
        val User=FirebaseAuth.getInstance().currentUser!!.uid
        val query =reference.child("user").orderByKey().equalTo(User)

        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(singleSnapshot in snapshot.children){
                    val name =singleSnapshot.child("name").value
                    val status=singleSnapshot.child("status").value
                    val profilphoto=singleSnapshot.child("downloadUrl").value

                    binding.profileName.text= name.toString()
                    binding.status.text=status.toString()
                    Picasso.get().load(profilphoto.toString()).into(binding.selectImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}