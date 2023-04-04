package com.example.humblr.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.humblr.R
import com.example.humblr.models.Post
import com.example.humblr.models.Vote
import com.example.humblr.utils.publishedTime
import kotlinx.coroutines.ExperimentalCoroutinesApi

class CommentsAdapter(
    private val _voteUp: (Post) -> Unit,
    private val _voteDown: (Post) -> Unit,
): RVAdapter<Post, CommentsAdapter.ViewHolderComment>() {

    inner class ViewHolderComment(view: View): BaseViewHolder<Post>(view){
        @ExperimentalCoroutinesApi

        private val _userName: TextView by lazy { itemView.findViewById(R.id.comment_user_name) }
        private val _commentBody: TextView by lazy { itemView.findViewById(R.id.comment_body) }
        private val _votesNumber: TextView by lazy { itemView.findViewById(R.id.tv_votes) }
        private val _voteUpBtn: ImageButton by lazy { itemView.findViewById(R.id.btn_vote) }
        private val _voteDownBtn: ImageButton by lazy { itemView.findViewById(R.id.btn_unvote) }
        private val _publishedTime: TextView by lazy { itemView.findViewById(R.id.comment_published_time) }

        override fun bind(data: Post) {
            _userName.text = data.data.author
            _commentBody.text = data.data.body

            _publishedTime.text = data.data.created!!.publishedTime()

            _votesNumber.text = when (data.data.score.toString().length) {
                4 -> data.data.score.toString().substring(0, 1) + "k"
                5 -> data.data.score.toString().substring(0, 2) + "k"
                6 -> data.data.score.toString().substring(0, 3) + "k"
                else -> data.data.score.toString()
            }

                when(data.data.user_voted){
                    Vote.VOTED_UP -> {
                        _voteUpBtn.setImageResource(R.drawable.icon_vote_clicked)
                        _voteDownBtn.setImageResource(R.drawable.icon_unvote_unclicked)
                    }
                    Vote.VOTED_DOWN -> {
                        _voteUpBtn.setImageResource(R.drawable.icon_vote_unclicked)
                        _voteDownBtn.setImageResource(R.drawable.icon_unvote_clicked)
                    }
                    Vote.DEFAULT -> {
                        _voteUpBtn.setImageResource(R.drawable.icon_vote_unclicked)
                        _voteDownBtn.setImageResource(R.drawable.icon_unvote_unclicked)
                    }
                }

            _voteUpBtn.setOnClickListener {
                _voteUp(data)
                when(data.data.user_voted){
                    Vote.VOTED_UP -> {
                        data.data.user_voted = Vote.DEFAULT
                        _voteUpBtn.setImageResource(R.drawable.icon_vote_unclicked)
                        data.data.score = data.data.score?.minus(1)
                    }
                    Vote.VOTED_DOWN -> {
                        data.data.user_voted = Vote.DEFAULT
                        _voteUpBtn.setImageResource(R.drawable.icon_vote_unclicked)
                        _voteDownBtn.setImageResource(R.drawable.icon_unvote_unclicked)
                        data.data.score = data.data.score?.plus(1)
                    }
                    Vote.DEFAULT -> {
                        data.data.user_voted = Vote.VOTED_UP
                        _voteUpBtn.setImageResource(R.drawable.icon_vote_clicked)
                        data.data.score = data.data.score?.plus(1)
                    }
                }
                _voteUp(data)
                notifyItemChanged(position)
            }

            _voteDownBtn.setOnClickListener {
                when(data.data.user_voted){
                    Vote.VOTED_UP -> {
                        data.data.user_voted = Vote.DEFAULT
                        _voteUpBtn.setImageResource(R.drawable.icon_vote_unclicked)
                        _voteDownBtn.setImageResource(R.drawable.icon_unvote_unclicked)
                        data.data.score = data.data.score?.minus(1)
                    }
                    Vote.VOTED_DOWN -> {
                        data.data.user_voted = Vote.DEFAULT
                        _voteDownBtn.setImageResource(R.drawable.icon_unvote_unclicked)
                        data.data.score = data.data.score?.plus(1)
                    }
                    Vote.DEFAULT -> {
                        data.data.user_voted = Vote.VOTED_DOWN
                        _voteDownBtn.setImageResource(R.drawable.icon_unvote_clicked)
                        data.data.score = data.data.score?.minus(1)
                    }
                }
                _voteDown(data)
                notifyItemChanged(position)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) =
        ViewHolderComment(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_comments_reply,
                parent,
                false
            )
        )
}