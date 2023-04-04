package com.example.humblr.models

data class MyAccountModel(
    val subreddit: MyProfile
)

data class MyProfile(
    val display_name_prefixed: String,
    val icon_img: String
)