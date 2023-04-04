package com.example.humblr.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.humblr.R
import com.example.humblr.databinding.FragmentSubredditBinding
import com.example.humblr.utils.Resource
import com.example.humblr.viewmodels.SubredditViewModel
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val SUBREDDIT_ID = "subreddit_id"

class SubredditFragment : Fragment() {
    private var subreddit_id: String? = null
    private val _subredditViewModel: SubredditViewModel by viewModel()
    private lateinit var _binding: FragmentSubredditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            subreddit_id = it.getString(SUBREDDIT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSubredditBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        lifecycleScope.launchWhenCreated {
            _subredditViewModel.subreddit.collect { subreddit ->

                when (subreddit) {
                    is Resource.Loading -> {
                        _binding.flProgress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        _binding.titleTop.text = "/r/${subreddit.data!!.data.display_name}"
                        _binding.subredditName.text = "/r/${subreddit.data!!.data.display_name}"
                        _binding.subredditTitle.text = subreddit.data!!.data.title
                        _binding.subredditDescription.text = subreddit.data!!.data.description

                        _binding.titleTop.text = subreddit.data.data.display_name_prefixed
                        _binding.shareBtn.setOnClickListener {
                            shareLink(subreddit.data.data.url)
                        }

                        Glide.with(requireContext()).load(subreddit.data!!.data.icon_img)
                            .into(_binding.subredditImage)

                        if (subreddit.data!!.data.user_is_subscriber) {
                            _binding.subscribeBtn.setBackgroundResource(R.drawable.item_unsubscribe_background)
                            _binding.ivSubscribe.setImageResource(R.drawable.icon_subscribed_white)
                            _binding.tvSubscribe.text = getString(R.string.subscribed)
                        } else {
                            _binding.subscribeBtn.setBackgroundResource(R.drawable.item_subscribe_background)
                            _binding.ivSubscribe.setImageResource(R.drawable.icon_unsubscribed_white)
                            _binding.tvSubscribe.text = getString(R.string.subscribe)
                        }

                        _binding.subscribeBtn.setOnClickListener {
                            if (subreddit.data!!.data.user_is_subscriber) {
                                _binding.subscribeBtn.setBackgroundResource(R.drawable.item_subscribe_background)
                                _binding.ivSubscribe.setImageResource(R.drawable.icon_unsubscribed_white)
                                _binding.tvSubscribe.text = getString(R.string.subscribe)
                                subreddit.data!!.data.user_is_subscriber = false
                                _subredditViewModel.unsubscribe(subreddit.data!!.data.display_name)
                            } else {
                                _binding.subscribeBtn.setBackgroundResource(R.drawable.item_unsubscribe_background)
                                _binding.ivSubscribe.setImageResource(R.drawable.icon_subscribed_white)
                                _binding.tvSubscribe.text = getString(R.string.subscribed)
                                subreddit.data!!.data.user_is_subscriber = true
                                _subredditViewModel.subscribe(subreddit.data!!.data.display_name)
                            }
                        }

                        _binding.flProgress.visibility = View.GONE
                    }
                    is Resource.Error -> {

                    }
                }
            }
        }

        _subredditViewModel.getSubreddit(subreddit_id!!)
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

        @JvmStatic
        fun newInstance(subreddit_id: String) =
            SubredditFragment().apply {
                arguments = Bundle().apply {
                    putString(SUBREDDIT_ID, subreddit_id)
                }
            }
    }
}