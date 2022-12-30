package com.example.chatapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.data.Message
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MessageAdapter(val context:Context,val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val ITEM_RECEIVE =1
    val ITEM_SENT=2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            val view:View= LayoutInflater.from(context).inflate(R.layout.receive_layout,parent,false)
            return ReceiveViewHolder(view)

        }else{
            val view:View= LayoutInflater.from(context).inflate(R.layout.sent_layout,parent,false)
            return SentViewHolder(view)

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if(holder.javaClass == SentViewHolder::class.java){

            val viewHolder=holder as SentViewHolder
            if(currentMessage.imageUrl != null){
            holder.sentİmage.visibility=View.VISIBLE
            holder.sentMessage.text =currentMessage.message
            holder.sentTime.text=currentMessage.time
            Picasso.get().load(currentMessage.imageUrl).into(holder.sentİmage)
            }else{
                holder.sentMessage.text =currentMessage.message
                holder.sentTime.text=currentMessage.time
            }


        }else{
            val viewHolder =holder as ReceiveViewHolder
            if(currentMessage.imageUrl != null){
            holder.receiveİmage.visibility=View.VISIBLE
            holder.receiveMessage.text=currentMessage.message
            holder.receiveTime.text=currentMessage.time
            Picasso.get().load(currentMessage.imageUrl).into(holder.receiveİmage)
            }else{
                holder.receiveMessage.text=currentMessage.message
                holder.receiveTime.text=currentMessage.time
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage=messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }else{
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
    class SentViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val sentMessage=itemView.findViewById<TextView>(R.id.txt_sent_message)
        val sentTime=itemView.findViewById<TextView>(R.id.txt_sent_time)
        val sentİmage=itemView.findViewById<ImageView>(R.id.txt_sent_image)

    }
    class ReceiveViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val receiveMessage=itemView.findViewById<TextView>(R.id.txt_receive_message)
        val receiveTime=itemView.findViewById<TextView>(R.id.txt_receive_time)
        val receiveİmage=itemView.findViewById<ImageView>(R.id.txt_receive_image)

    }
}