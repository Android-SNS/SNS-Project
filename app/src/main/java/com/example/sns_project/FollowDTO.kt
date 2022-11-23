package com.example.sns_project

data class FollowDTO(
    var follwerCount : Int = 0 ,  //팔로워 수
    var followers : MutableMap<String,Boolean> = HashMap(), // 중복방지

    var follwingCount : Int = 0 , // 팔로윙 수
    var followings : MutableMap<String,Boolean> = HashMap(), //  중복 방지
)