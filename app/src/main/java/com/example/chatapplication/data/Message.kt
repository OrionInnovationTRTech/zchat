package com.example.chatapplication.data

class Message {
    var message:String?= null
    var senderId:String?=null
    var time:String?=null
    var imageUrl:String?=null

    constructor(){}

    constructor(message:String?, senderId:String?, time: String?,imageUrl:String?){
        this.message=message
        this.senderId=senderId
        this.time=time
        this.imageUrl=imageUrl

    }
}