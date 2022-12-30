package com.example.chatapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.data.User
import com.example.chatapplication.logInFragments.UserFragmentDirections
import com.squareup.picasso.Picasso


class UserAdapter(val context:Context, val userList:ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view:View=LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false)
        return UserViewHolder(view)


    }
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser=userList[position]
        holder.textName.text=currentUser.name
        holder.textStat.text=currentUser.status
        Picasso.get().load(currentUser.downloadUrl).into(holder.imageUser)
        val name=currentUser.name.toString()
        val uid=currentUser.uid.toString()
        val url=currentUser.downloadUrl.toString()
        holder.itemView.setOnClickListener{ view ->
            val action = UserFragmentDirections.actionUserFragmentToTalkFragment(name,uid,url)
            Navigation.findNavController(view).navigate(action)
         }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
    class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val textName=itemView.findViewById<TextView>(R.id.txt_name)
        val textStat=itemView.findViewById<TextView>(R.id.txt_status)
        val imageUser=itemView.findViewById<ImageView>(R.id.img_user)
    }

}