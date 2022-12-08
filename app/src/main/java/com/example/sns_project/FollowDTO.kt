package com.example.sns_project

data class FollowDTO(
    var followerCount : Int = 0 ,  //팔로워 수
    var followers : MutableMap<String,Boolean> = HashMap(), // 중복방지

    var followingCount : Int = 0 , // 팔로윙 수
    var followings : MutableMap<String,Boolean> = HashMap(), //  중복 방지
)