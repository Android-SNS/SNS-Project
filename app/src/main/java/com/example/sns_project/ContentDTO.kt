package com.example.sns_project

data class ContentDTO(
    var explain:String? = null, // 설명관리
    var imageUrl:String? = null, // url 저장
    var uid:String? = null, // 어떤 유저가 올렸는지 관리
    var userId:String? = null, // 유저의 아이디
    var nickname: String? = null, // 유저의 닉네임
    var timestamp:Long? = null, // 몇시 몇분에 올렸는지 관리
    var favoriteCount:Int? = 0, //  좋아요 개수
    var favorites:Map<String,Boolean> = HashMap() // 누가 좋아요 눌렀는 지
)