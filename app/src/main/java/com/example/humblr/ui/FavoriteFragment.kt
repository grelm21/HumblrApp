package com.example.humblr.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.humblr.R
import com.example.humblr.adapters.FeedAdapter
import com.example.humblr.adapters.SubredditsAdapter
import com.example.humblr.databinding.FragmentFavoriteBinding
import com.example.humblr.utils.Resource
import com.example.humblr.viewmodels.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {

    private lateinit var _binding: FragmentFavoriteBinding
    private val _favoritesViewModel: FavoritesViewModel by viewModel()
    private val _savedAdapter: FeedAdapter by lazy {
        FeedAdapter(
            _onClick = { post ->
                val postFragment = PostFragment.newInstance(post.data.id!!)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_nav_host_fragment, postFragment)
                    .setCustomAnimations(androidx.navigation.ui.R.anim.nav_default_enter_anim, androidx.navigation.ui.R.anim.nav_default_exit_anim)
                    .addToBackStack("post")
                    .commit()
            },
            _voteUp = { post ->

            },
            _voteDown = { post ->

            }
        )
    }

    private val _subredditsAdapter: SubredditsAdapter by lazy {SubredditsAdapter{subreddit ->
        if(subreddit.data.user_is_subscriber){
            _favoritesViewModel.unsubscribe(subreddit.data.display_name)
        }else{
            _favoritesViewModel.subscribe(subreddit.data.display_name)
        }
    }}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFavoriteBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            _favoritesViewModel.saved.collect{
                when(it){
                    is Resource.Loading -> {
                        _binding.flProgress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        _binding.recyclerPosts.adapter = _savedAdapter
                        _savedAdapter.submitList(it.data!!.data.children)
                        _binding.flProgress.visibility = View.GONE
                        _binding.flError.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        _binding.flProgress.visibility = View.GONE
                        _binding.flError.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            _favoritesViewModel.profile.collect{
                when(it){
                    is Resource.Success -> {
                        _favoritesViewModel.getSavedPosts(it.data!!.subreddit.display_name_prefixed.substring(2))
                        _binding.flError.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        _binding.flProgress.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {
                        _binding.flProgress.visibility = View.GONE
                        _binding.flError.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            _favoritesViewModel.subreddits.collect{

                when(it){
                    is Resource.Loading -> {
                        _binding.flProgress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        _binding.recyclerPosts.adapter = _subredditsAdapter
                        _subredditsAdapter.submitList(it.data!!.data.children)
                        _binding.flProgress.visibility = View.GONE
                        _binding.flError.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        _binding.flProgress.visibility = View.GONE
                        _binding.flError.visibility = View.VISIBLE
                    }
                }
            }
        }

        _binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.btn_subreddits -> {
                    _favoritesViewModel.getUserSubreddits()
                    _binding.flProgress.visibility = View.VISIBLE
                }
                R.id.btn_posts -> {
                    _favoritesViewModel.getProfile()
                    _binding.flProgress.visibility = View.VISIBLE
                }
            }
        }
        _favoritesViewModel.getUserSubreddits()
    }
}