package com.example.humblr.models

data class UserCommentsModel (
    val data: CommentsData
    )

data class CommentsData(
    val after: String?,
    val before: String?,
    val children: List<Comment>
)

data class Comment(
    val kind: String,
    val data: CommentBody
)

data class CommentBody (
    val link_author: String,
    val link_title: String,
    val body: String,
    val link_id: String,
    val author: String
)