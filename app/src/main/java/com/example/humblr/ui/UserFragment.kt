package com.example.humblr.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.humblr.R
import com.example.humblr.adapters.UserCommentsAdapter
import com.example.humblr.databinding.FragmentUserBinding
import com.example.humblr.models.Comment
import com.example.humblr.models.CommentsData
import com.example.humblr.utils.FeedState
import com.example.humblr.utils.PaginationScrollListener
import com.example.humblr.utils.Resource
import com.example.humblr.viewmodels.UserViewModel
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val USER_ID = "user_id"

class UserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var userId: String? = null
    private lateinit var _binding: FragmentUserBinding
    private val _userViewModel: UserViewModel by viewModel()
    private val _commentsAdapter: UserCommentsAdapter by lazy { UserCommentsAdapter() }
    private var isLoading = false
    private val _comments = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        _binding.rvComments.adapter = _commentsAdapter

        lifecycleScope.launchWhenCreated {
            _userViewModel.user.collect { user ->

                when (user) {
                    is Resource.Loading -> {
                        _binding.flProgress.visibility = VISIBLE
                    }
                    is Resource.Success -> {
                        _binding.profileName.text = user.data!!.data.subreddit.display_name_prefixed

                        _binding.titleTop.text = user.data.data.subreddit.display_name_prefixed
                        _binding.shareBtn.setOnClickListener {
                            shareLink(user.data.data.subreddit.url)
                        }

                        Glide.with(requireContext()).load(user.data.data.subreddit.icon_img)
                            .placeholder(R.drawable.profile_icon_black)
                            .into(_binding.profileImage)
                    }
                    is Resource.Error -> {
                        _binding.flProgress.visibility = GONE
                        Toast.makeText(requireContext(), user.message, Toast.LENGTH_SHORT).show()
                    }
                }

                if (user.data!!.data.is_friend) {
                    _binding.subscribeBtn.setBackgroundResource(R.drawable.item_unsubscribe_background)
                    _binding.ivSubscribe.setImageResource(R.drawable.icon_subscribed_white)
                    _binding.profileName.background = resources.getDrawable(R.drawable.text_view_item_subscribed)
                    _binding.tvSubscribe.text = getString(R.string.you_re_friends)
                } else {
                    _binding.subscribeBtn.setBackgroundResource(R.drawable.item_subscribe_background)
                    _binding.ivSubscribe.setImageResource(R.drawable.icon_unsubscribed_white)
                    _binding.profileName.background = resources.getDrawable(R.drawable.text_view_item_unsubscribed)
                    _binding.tvSubscribe.text = getString(R.string.add_to_friends)
                }

                _binding.subscribeBtn.setOnClickListener {
                    if (user.data!!.data.is_friend) {
                        _binding.subscribeBtn.setBackgroundResource(R.drawable.item_subscribe_background)
                        _binding.ivSubscribe.setImageResource(R.drawable.icon_unsubscribed_white)
                        _binding.tvSubscribe.text = getString(R.string.add_to_friends)
                        _binding.profileName.background = resources.getDrawable(R.drawable.text_view_item_unsubscribed)
                        user.data!!.data.is_friend = false
                    } else {
                        _binding.subscribeBtn.setBackgroundResource(R.drawable.item_unsubscribe_background)
                        _binding.ivSubscribe.setImageResource(R.drawable.icon_subscribed_white)
                        _binding.tvSubscribe.text = getString(R.string.you_re_friends)
                        _binding.profileName.background = resources.getDrawable(R.drawable.text_view_item_subscribed)
                        user.data!!.data.is_friend = true
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            _userViewModel.comments.collect { comments ->
                when (comments) {
                    is Resource.Loading -> {
                        _binding.flProgress.visibility = VISIBLE
                    }
                    is Resource.Success -> {
                        isLoading = false
                        _binding.flProgress.visibility = GONE
                        _comments.addAll(comments.data!!.data.children)
                        _commentsAdapter.submitList(_comments as List<Comment>)
                        _commentsAdapter.notifyDataSetChanged()
                        paging(userId!!, comments.data.data.after)
                    }
                    is Resource.Error -> {
                        _binding.flProgress.visibility = GONE
                        Toast.makeText(requireContext(), comments.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        _userViewModel.getUserCommentsCheck(userId!!)
        _userViewModel.getUser(userId!!)
    }

    private fun paging(author: String, after: String?) {
        _binding.rvComments.addOnScrollListener(object :
            PaginationScrollListener(_binding.rvComments.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                _userViewModel.getUserCommentsCheck(author = author, after = after)
                _binding.flProgress.visibility = View.VISIBLE
                isLoading = true
            }

            override fun isLoading() = isLoading
        })
    }

    private fun shareLink(url: String) {
        val urlLink = "https://www.reddit.com" + url
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, urlLink)
        intent.type = "text/plain"
        val shareIntent =
            Intent.createChooser(intent,requireContext().getString(R.string.share_link_text))
        context?.startActivity(shareIntent)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userId: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_ID, userId)
                }
            }
    }
}