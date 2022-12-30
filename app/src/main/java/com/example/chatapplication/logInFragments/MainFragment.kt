package com.example.chatapplication.logInFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.chatapplication.activity.MainActivity
import com.example.chatapplication.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainSigninButton.setOnClickListener{
            val action =MainFragmentDirections.actionMainFragmentToLogInFragment()
            Navigation.findNavController(view).navigate(action)
        }
        binding.mainSignupButton.setOnClickListener{
            val action =MainFragmentDirections.actionMainFragmentToSignupFragment()
            Navigation.findNavController(view).navigate(action)

        }


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}