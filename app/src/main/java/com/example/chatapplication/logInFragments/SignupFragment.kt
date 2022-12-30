package com.example.chatapplication.logInFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatapplication.databinding.FragmentSignupBinding
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.chatapplication.activity.MainActivity
import com.example.chatapplication.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*


class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var mDbREf: DatabaseReference

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    var selectedPicture: Uri? = null
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth= Firebase.auth
        storage= Firebase.storage
        registerLauncher()

        binding.selectImageView.setOnClickListener{
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }else{
                val intentToGallery= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }

        }



        binding.SignupButton.setOnClickListener {
            val name = binding.SignupNameText.text.toString()
            val email =binding.SignupMailText.text.toString()
            val password=binding.SignupPassText.text.toString()
            val status=binding.statusText.text.toString()

            val uuid= UUID.randomUUID()
            val imageName="$uuid.jpg"

            val reference=storage.reference
            val imageReference=reference.child("image").child(imageName)

            if(selectedPicture != null)
                imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                    val uploadPictureReference = storage.reference.child("image").child(imageName)
                    uploadPictureReference.downloadUrl.addOnSuccessListener{
                        val downloadUrl=it.toString()
                        signUp(name,email,password,status,downloadUrl)
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(),"download url referans alamadın", Toast.LENGTH_LONG).show()

                    }

                }.addOnFailureListener{
                    Toast.makeText(requireContext(),it.localizedMessage, Toast.LENGTH_LONG).show()
                }


        }

    }


    private fun signUp(name:String,email:String,password:String,status:String,downloadUrl:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {

                    addUserToDatabase(name,email,auth.currentUser?.uid!!,status,downloadUrl)
                    Toast.makeText(requireContext(),"Hesabınız kaydedildi", Toast.LENGTH_LONG).show()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(requireContext(),"Hesap kaydedilemedi", Toast.LENGTH_LONG).show()


                }
            }

    }
    private fun addUserToDatabase(name:String,email:String,uid:String,status:String,downloadUrl:String){
        mDbREf= FirebaseDatabase.getInstance().getReference()

        mDbREf.child("user").child(uid).setValue(User(name,email,uid,status,downloadUrl))


    }
    private fun registerLauncher(){

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode== AppCompatActivity.RESULT_OK){
                val intentFromResult =result.data
                if(intentFromResult != null){
                    selectedPicture =intentFromResult.data
                    selectedPicture?.let{
                        binding.selectImageView.setImageURI(it)

                    }
                }
            }
        }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if(result){
                val intentToGallery= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }else{
                Toast.makeText(requireContext(),"izin gerekli", Toast.LENGTH_LONG).show()
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

