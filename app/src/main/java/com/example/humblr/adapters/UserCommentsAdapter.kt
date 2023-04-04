package com.example.humblr.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.humblr.R
import com.example.humblr.models.Comment
import com.example.humblr.models.Post
import kotlinx.coroutines.ExperimentalCoroutinesApi

class UserCommentsAdapter(): RVAdapter<Comment, UserCommentsAdapter.ViewHolderUserComments>() {

    inner class ViewHolderUserComments(view: View): BaseViewHolder<Comment>(view){
        @ExperimentalCoroutinesApi

        private val _authorName: TextView by lazy { itemView.findViewById(R.id.user_name_author) }
        private val _postTitle: TextView by lazy { itemView.findViewById(R.id.title_author) }
        private val _userName: TextView by lazy { itemView.findViewById(R.id.comment_user_name) }
        private val _userComment: TextView by lazy { itemView.findViewById(R.id.comment_body) }

        override fun bind(data: Comment) {
            _authorName.text = "/r/${ data.data.link_author }"
            _postTitle.text = data.data.link_title
            _userName.text = "/r/${data.data.author}"
            _userComment.text = data.data.body
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderUserComments =
        ViewHolderUserComments(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user_comments,
                parent,
                false
            )
        )
}