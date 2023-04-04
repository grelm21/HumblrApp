package com.example.humblr.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.humblr.R
import com.example.humblr.adapters.CommentsAdapter
import com.example.humblr.databinding.FragmentPostBinding
import com.example.humblr.models.DataPost
import com.example.humblr.models.Post
import com.example.humblr.models.RedditVideo
import com.example.humblr.models.Vote
import com.example.humblr.utils.Resource
import com.example.humblr.utils.publishedTime
import com.example.humblr.viewmodels.PostViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.MimeTypes
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val POST_ID = "postId"

class PostFragment : Fragment() {

    private var postId: String? = null
    private val _postViewModel: PostViewModel by viewModel()
    private lateinit var _binding: FragmentPostBinding
    private var exoPlayer: ExoPlayer? = null
    private val _commentAdapter: CommentsAdapter by lazy {
        CommentsAdapter(
            _voteUp = { post ->
                _postViewModel.voteUp(post.data.name)
            },
            _voteDown = { post ->
                _postViewModel.voteDown(post.data.name)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getString(POST_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentPostBinding.inflate(inflater)
        return _binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exoPlayer?.release()

        _binding.backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        _binding.rvComments.adapter = _commentAdapter

        lifecycleScope.launchWhenCreated {
            _postViewModel.post.collect {
                when (it) {
                    is Resource.Loading -> {
                        _binding.flProgress.visibility = VISIBLE
                    }
                    is Resource.Success -> {
                        val post = it.data?.get(0)?.data?.children?.get(0)?.data!!

                        _binding.flError.visibility = View.GONE

                        _postViewModel.getUser(post.author!!)
                        _postViewModel.getSubreddit(post.subreddit!!)
                        setUpPost(post)

                        _binding.subredditName.setOnClickListener {
                            val fragment = SubredditFragment.newInstance(post.subreddit)
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.main_nav_host_fragment, fragment)
                                .addToBackStack("subreddit").commit()
                        }

                        _binding.userName.setOnClickListener {
                            val fragment = UserFragment.newInstance(post.author)
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.main_nav_host_fragment, fragment)
                                .addToBackStack("user").commit()
                        }

                        _binding.buttonSave.setImageResource(if (post.saved == false) R.drawable.baseline_save_24 else R.drawable.baseline_delete_24)
                        _binding.tvSave.text = if (post.saved == false) resources.getString(R.string.save) else resources.getString(R.string.unsave)

                        _binding.buttonSave.setOnClickListener {
                            if (post.saved == false) {
                                _binding.buttonSave.setImageResource(R.drawable.baseline_delete_24)
                                _postViewModel.savePost(post.name)
                                _binding.tvSave.text = resources.getString(R.string.unsave)
                                post.saved = true
                            } else {
                                _binding.buttonSave.setImageResource(R.drawable.baseline_save_24)
                                _postViewModel.unsavePost(post.name)
                                _binding.tvSave.text = resources.getString(R.string.save)
                                post.saved = true
                            }
                        }

                        if (it.data[1]?.data?.children?.size!! > 0) {
                            val commentList = it.data?.get(1)?.data?.children
                            if (commentList?.size!! > 0) {
                                val lastIndex = commentList.lastIndex
                                val commentListSubmit = commentList.subList(0, lastIndex)
                                _commentAdapter.submitList(commentListSubmit)
                            }

                            if (commentList.last().kind == "more") {
                                _binding.allComments.visibility = VISIBLE
                            }
                        } else {
                            _binding.noComments.visibility = VISIBLE
                        }
                    }
                    is Resource.Error -> {
                        _binding.flProgress.visibility = View.GONE
                        _binding.flError.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            _postViewModel.user.collect {
                Glide.with(requireContext()).load(it.data?.data?.subreddit?.icon_img)
                    .placeholder(R.drawable.baseline_person_24)
                    .into(_binding.userSubscribeBtn)

                _binding.userName.text = it.data!!.data.subreddit.display_name_prefixed

                _binding.cvPost.visibility = VISIBLE
                _binding.flProgress.visibility = GONE
            }
        }

        lifecycleScope.launchWhenCreated {
            _postViewModel.subreddit.collect { subreddit ->
                _binding.subredditSubscribeBtn.setImageResource(if (subreddit.data!!.data.user_is_subscriber) R.drawable.icon_subscribed else R.drawable.icon_unsubscribed)

                _binding.subredditSubscribeBtn.setOnClickListener {
                    if (subreddit.data!!.data.user_is_subscriber) {
                        _binding.subredditSubscribeBtn.setImageResource(R.drawable.icon_unsubscribed)
                        subreddit.data!!.data.user_is_subscriber = false

                        _postViewModel.unsubscribe(subreddit.data!!.data.display_name)
                        Toast.makeText(
                            requireContext(),
                            "You unsubscribed from ${subreddit.data!!.data.display_name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        _binding.subredditSubscribeBtn.setImageResource(R.drawable.icon_subscribed)
                        subreddit.data!!.data.user_is_subscriber = true

                        _postViewModel.subscribe(subreddit.data!!.data.display_name)
                        Toast.makeText(
                            requireContext(),
                            "You subscribed to ${subreddit.data!!.data.display_name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        _postViewModel.getPost(postId!!)
    }

//    private fun setUpComment(comment: DataPost){
//        _binding.apply {
//            commentUserName.visibility = VISIBLE
//            commentProfileImage.visibility = VISIBLE
//            commnetThreadSeparator.visibility = VISIBLE
//            commentPublishedTime.visibility = VISIBLE
//
//            commentUserName.text = comment.author
//            commentBody.text = comment.body
//        }
//    }

    private fun setUpPost(post: DataPost) {
        _binding.apply {
            titleTop.text = post.title
            subredditName.text = post.subreddit_name_prefixed
//            userName.text = post.author
            titleBody.text = post.title

//            if (post.is_video == true) {
//                post?.media?.reddit_video?.let { initVideo(it) }
//                player.visibility = VISIBLE
//            } else {
            if(post.is_video == false){
                if (!post.selftext.isNullOrEmpty()) {
                    descriptionBody.text = post.selftext
                    descriptionBody.visibility = VISIBLE
                }
            }

            publishedTime.text = post.created!!.publishedTime()

            _binding.shareBtn.setOnClickListener {
                shareLink(post.url!!)
            }

            tvVotes.text = when (post.score.toString().length) {
                4 -> post.score.toString().substring(0, 1) + "k"
                5 -> post.score.toString().substring(0, 2) + "k"
                6 -> post.score.toString().substring(0, 3) + "k"
                else -> post.score.toString()
            }

            btnVote.setOnClickListener {
                when (post.user_voted) {
                    Vote.VOTED_UP -> {
                        post.user_voted = Vote.DEFAULT
                        btnVote.setImageResource(R.drawable.icon_vote_unclicked)
                        post.score = post.score?.minus(1)
                        _postViewModel.voteDown(post.name)
                        tvVotes.text = post.score.toString()
                    }
                    Vote.VOTED_DOWN -> {
                        post.user_voted = Vote.DEFAULT
                        btnVote.setImageResource(R.drawable.icon_vote_unclicked)
                        btnUnvote.setImageResource(R.drawable.icon_unvote_unclicked)
                        post.score = post.score?.plus(1)
                        _postViewModel.voteUp(post.name)
                        tvVotes.text = post.score.toString()
                    }
                    Vote.DEFAULT -> {
                        post.user_voted = Vote.VOTED_UP
                        btnVote.setImageResource(R.drawable.icon_vote_clicked)
                        post.score = post.score?.plus(1)
                        _postViewModel.voteUp(post.name)
                        tvVotes.text = post.score.toString()
                    }
                }
            }

            btnUnvote.setOnClickListener {
                when (post.user_voted) {
                    Vote.VOTED_UP -> {
                        post.user_voted = Vote.DEFAULT
                        btnVote.setImageResource(R.drawable.icon_vote_unclicked)
                        btnUnvote.setImageResource(R.drawable.icon_unvote_unclicked)
                        post.score = post.score?.minus(1)
                        _postViewModel.voteDown(post.name)
                        tvVotes.text = post.score.toString()
                    }
                    Vote.VOTED_DOWN -> {
                        post.user_voted = Vote.DEFAULT
                        btnUnvote.setImageResource(R.drawable.icon_unvote_unclicked)
                        post.score = post.score?.plus(1)
                        _postViewModel.voteUp(post.name)
                        tvVotes.text = post.score.toString()
                    }
                    Vote.DEFAULT -> {
                        post.user_voted = Vote.VOTED_DOWN
                        btnUnvote.setImageResource(R.drawable.icon_unvote_clicked)
                        post.score = post.score?.minus(1)
                        _postViewModel.voteDown(post.name)
                        tvVotes.text = post.score.toString()
                    }
                }
            }
        }
    }

    private fun shareLink(url: String) {
        val urlLink = url
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, urlLink)
        intent.type = "text/plain"
        val shareIntent =
            Intent.createChooser(intent,requireContext().getString(R.string.share_link_text))
        context?.startActivity(shareIntent)
    }

    companion object {
        @JvmStatic
        fun newInstance(postId: String) =
            PostFragment().apply {
                arguments = Bundle().apply {
                    putString(POST_ID, postId)
                }
            }
    }
}