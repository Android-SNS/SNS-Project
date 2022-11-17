package com.example.sns_project

data class ContentDTO(
    var explain:String? = null, // 설명관리
    var imageUrl:String? = null, // url 저장
    var uid:String? = null, // 어떤 유저가 올렸는지 관리
    var userId:String? = null, // 유저의 아이디
    var nickname: String? = null,
    var timestamp:Long? = null, // 몇시 몇분에 올렸는지 관리
    var favoriteCount:Int? = 0, //  좋아요 개수
    var favorites:Map<String,Boolean> = HashMap() // 누가 좋아요 눌렀는 지
){
    data class Comment( // 나중에 댓글 남겼을 때 데이터 관리를 위해
        var uid: String? = null, // 누가 남겼는 지
        var userId: String? = null, // 댓글 남긴 유저의 아이디
        var comment:String? = null, // 뭐라고 남겼는 지
        var timestamp: Long? = null) // 몇시 몇분에 올렸는지지
}