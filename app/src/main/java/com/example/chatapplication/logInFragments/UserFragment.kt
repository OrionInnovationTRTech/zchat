package com.example.chatapplication.logInFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.adapter.UserAdapter
import com.example.chatapplication.data.User
import com.example.chatapplication.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class UserFragment : Fragment() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList:ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var DbRef: DatabaseReference



    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        auth= FirebaseAuth.getInstance()
        DbRef= FirebaseDatabase.getInstance().getReference()

        userList=ArrayList()
        adapter=UserAdapter(requireContext(),userList)

        userRecyclerView=binding.userRecyclerView
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.adapter=adapter

        DbRef.child("user").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for(postSnapShot in snapshot.children) {

                    val currentUser = postSnapShot.getValue(User::class.java)
                    if(auth.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()

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