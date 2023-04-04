package com.example.humblr.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.humblr.R
import com.example.humblr.models.Post
import com.example.humblr.models.Vote
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView.SHOW_BUFFERING_NEVER
import com.google.android.exoplayer2.util.MimeTypes
import kotlinx.coroutines.ExperimentalCoroutinesApi

class FeedAdapter(
    private val _onClick: (Post) -> Unit,
    private val _voteUp: (Post) -> Unit,
    private val _voteDown: (Post) -> Unit,
) : RVAdapter<Post, FeedAdapter.ViewHolderFeed>() {

//    private var exoPlayer: ExoPlayer? = null
    private var unfolded = false

    inner class ViewHolderFeed(view: View) : BaseViewHolder<Post>(view) {
        @ExperimentalCoroutinesApi

        private val _title: TextView by lazy { itemView.findViewById(R.id.title) }
        private val _text: TextView by lazy { itemView.findViewById(R.id.text) }
        private val _player: StyledPlayerView by lazy { itemView.findViewById(R.id.player) }
        private val _author: TextView by lazy { itemView.findViewById(R.id.username) }
        private val _commentsCount: TextView by lazy { itemView.findViewById(R.id.comments_count) }
        private val _infoLayout: ConstraintLayout by lazy { itemView.findViewById(R.id.user_comment_layout) }
        private val _content: FrameLayout by lazy { itemView.findViewById(R.id.content) }
        private val _votes: TextView by lazy { itemView.findViewById(R.id.tv_votes) }
        private val _voteUpBtn: ImageButton by lazy { itemView.findViewById(R.id.btn_vote) }
        private val _voteDownBtn: ImageButton by lazy { itemView.findViewById(R.id.btn_unvote) }

        override fun bind(data: Post) {
            _title.text = data.data.title
            _author.text = data.data.author
            _commentsCount.text = data.data.num_comments.toString()
            _votes.text = when (data.data.score.toString().length) {
                4 -> data.data.score.toString().substring(0, 1) + "k"
                5 -> data.data.score.toString().substring(0, 2) + "k"
                6 -> data.data.score.toString().substring(0, 3) + "k"
                else -> data.data.score.toString()
            }

            _content.setOnClickListener {
                _onClick(data)
            }

            itemView.setOnClickListener {
                _onClick(data)
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

                if (data.data.is_video == false)
                    if (!data.data.selftext.isNullOrEmpty()) {
                        _text.visibility = VISIBLE
                        _text.text = data.data.selftext
                    }


//            _title.setOnClickListener {
//                if (!data.data.isUnfolded) {
//                    if (data.data.is_video == true) {
//                        initVideo(data)
//                    } else {
//                        if (!data.data.selftext.isNullOrEmpty()) {
//                            _text.visibility = VISIBLE
//                            _text.text = data.data.selftext
//                        }
//                    }
//                    data.data.isUnfolded = true
////                    notifyItemChanged(position)
//                } else {
//                    closeContent()
//                    data.data.isUnfolded = false
////                    notifyItemChanged(position)
//                }
//            }
        }

//        private fun initVideo(data: Post) {
//            _player.visibility = VISIBLE
//
//            exoPlayer = ExoPlayer.Builder(context).build()
//            _player.player = exoPlayer
//
//            _player.apply {
//                setShowFastForwardButton(false)
//                setShowNextButton(false)
//                setShowPreviousButton(false)
//                setShowRewindButton(false)
//                setShowShuffleButton(false)
//                setShowSubtitleButton(false)
//                setShowVrButton(false)
//                setShowBuffering(SHOW_BUFFERING_NEVER)
//            }
//
//            val mediaItem = MediaItem.Builder()
//                .setUri(data.data.media?.reddit_video?.hls_url)
//                .setMimeType(MimeTypes.APPLICATION_M3U8)
//                .build()
//
//            exoPlayer?.setMediaItem(mediaItem)
//            exoPlayer?.prepare()
////            exoPlayer?.play()
//        }

//        private fun closeContent() {
//            _player.visibility = GONE
//            _text.visibility = GONE
//            exoPlayer?.stop()
//            exoPlayer?.release()
//        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFeed =
        ViewHolderFeed(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_subreddit_feed,
                parent,
                false
            )
        )

}