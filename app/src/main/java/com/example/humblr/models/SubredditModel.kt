package com.example.humblr.models

data class SubredditList(
    val kind: String,
    val data: SubredditsData
)

data class SubredditsData(
    val children: List<SubredditModel>
)

data class SubredditModel(
    val kind: String,
    val data: DataSubreddit
)

data class DataSubreddit(
    var user_is_subscriber: Boolean,
    val name: String,
    val title: String,
    val display_name: String,
    val display_name_prefixed: String,
    val url: String,
    val icon_img: String,
    val description: String
)