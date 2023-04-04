package com.example.humblr.models

data class UserModel (
    val kind: String,
    val data: Data
)

data class Data(
    var is_friend: Boolean,
    val subreddit: Subreddit
)

data class Subreddit(
    val display_name_prefixed: String,
    val description: String,
    val url: String,
    val icon_img: String,
    val subscribers: Int
)