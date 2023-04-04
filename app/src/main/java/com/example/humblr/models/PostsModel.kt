package com.example.humblr.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PostsModel(
    val kind: String,
    val data: DataList,
)

data class DataList(
    val dist: Int?,
    val before: String?,
    val after: String?,
    val children: List<Post>
)

data class Post(
    val kind: String,
    val data: DataPost
)

data class DataPost(
    val id: String?,
    val title: String?,
    val name: String,
    val created: Long?,
    val url: String?,
    val author: String?,
    val selftext: String?,
    val num_comments: Int?,
    val media: Media?,
    val is_video: Boolean?,
    var score: Int?,
    val subreddit_name_prefixed: String?,
    val subreddit: String?,
    var saved: Boolean?,
    var isUnfolded: Boolean = false,
    val body: String?,
    var user_voted: Vote = Vote.DEFAULT
//    val children: List<Post>?
)

enum class Vote{
    VOTED_UP,
    VOTED_DOWN,
    DEFAULT
}

data class Media(
    val reddit_video: RedditVideo
)

data class RedditVideo(
    val bitrate_kbps: Int,
    val fallback_url: String,
    val height: Int,
    val width: Int,
    val scrubber_media_url: String,
    val dash_url: String,
    val duration: Int,
    val hls_url: String,
    val is_gif: Boolean
)
