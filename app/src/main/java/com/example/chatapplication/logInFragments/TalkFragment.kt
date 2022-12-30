package com.example.chatapplication.logInFragments

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplication.adapter.MessageAdapter
import com.example.chatapplication.data.Message
import com.example.chatapplication.databinding.FragmentTalkBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class TalkFragment : Fragment() {
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList:ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    var database:FirebaseDatabase?=null
    var receiverRoom:String?=null
    var senderRoom:String?=null
    private lateinit var storage: FirebaseStorage
    private lateinit var auth:FirebaseAuth
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null

    private var _binding: FragmentTalkBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database=FirebaseDatabase.getInstance()
        storage= Firebase.storage
        auth=Firebase.auth
        registerLauncher()


        binding.imageButton.setOnClickListener{
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }else{
                val intentToGallery= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }

        }

        arguments?.let{
            val name=TalkFragmentArgs.fromBundle(it).name
            val receiverUid=TalkFragmentArgs.fromBundle(it).uid
            val senderUid= FirebaseAuth.getInstance().currentUser?.uid
            val url=TalkFragmentArgs.fromBundle(it).url
            Picasso.get().load(url).into(binding.imgBox)
            binding.nameBox.text=name.toString()
            database!!.reference.child("presence").child(receiverUid!!).addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val status=snapshot.getValue(String::class.java)
                        if(status=="Offline"){
                            binding.OnOfBox.text="Offline"
                        }else{
                            binding.OnOfBox.text="Online"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            mDbRef= FirebaseDatabase.getInstance("https://z-chat-b6ab5-default-rtdb.firebaseio.com/").reference
            senderRoom=receiverUid+senderUid
            receiverRoom=senderUid+receiverUid

            messageList=ArrayList()
            messageAdapter= MessageAdapter(requireContext(),messageList)

            binding.chatRecyclerView.layoutManager= LinearLayoutManager(requireContext())
            binding.chatRecyclerView.adapter=messageAdapter
            //logic for adding data to recyclerView

            mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for(postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }


            })
            binding.talkbackImage.setOnClickListener{

                val action =TalkFragmentDirections.actionTalkFragmentToUserFragment()
                Navigation.findNavController(view).navigate(action)
            }

            binding.sendButton.setOnClickListener{
                val message =binding.messageBox.text.toString()
                val simpleDate = SimpleDateFormat("hh:mm")
                val time = simpleDate.format(Date())
                val imageUrl=null
                val messageObject=Message(message,senderUid,time,imageUrl)

                val uuid= UUID.randomUUID()
                val imageName="$uuid.jpg"

                val reference=storage.reference
                val imageReference=reference.child("image").child(imageName)
                if(selectedPicture != null){
                    imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                        val uploadPictureReference = storage.reference.child("image").child(imageName)
                        uploadPictureReference.downloadUrl.addOnSuccessListener{
                            val downloadUrl=it.toString()
                            val messageObj=Message(message,senderUid,time,downloadUrl)
                            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                                .setValue(messageObj).addOnSuccessListener {
                                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                                        .setValue(messageObj)

                                }
                            binding.messageBox.setText("")
                            binding.imageButton.setImageResource(android.R.drawable.ic_menu_camera)
                            selectedPicture=null

                        }.addOnFailureListener {
                            Toast.makeText(requireContext(),"download url referans alamadın", Toast.LENGTH_LONG).show()

                        }

                    }.addOnFailureListener{
                        Toast.makeText(requireContext(),it.localizedMessage, Toast.LENGTH_LONG).show()
                    }}else{
                   mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                      .setValue(messageObject).addOnSuccessListener {
                          mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                             .setValue(messageObject)
                     }
                  binding.messageBox.setText("")
               }
            }
        }
    }
    private fun registerLauncher(){

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode== AppCompatActivity.RESULT_OK){
                val intentFromResult =result.data
                if(intentFromResult != null){
                    selectedPicture =intentFromResult.data
                    selectedPicture?.let{
                        binding.imageButton.setImageURI(it)

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

}

