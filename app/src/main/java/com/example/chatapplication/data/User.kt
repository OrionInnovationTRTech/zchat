package com.example.chatapplication.data

 class User {
    var name:String? = null
    var email:String?=null
    var uid:String?=null
    var status:String?=null
    var downloadUrl:String?=null

    constructor(){}

    constructor(name:String?,email:String?,uid:String?,status:String?,downloadUrl:String?){
        this.name=name
        this.email=email
        this.uid= uid
        this.status=status
        this.downloadUrl=downloadUrl

    }
}