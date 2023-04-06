package com.example.humblr.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.humblr.R
import com.example.humblr.adapters.FeedAdapter
import com.example.humblr.databinding.FragmentFeedBinding
import com.example.humblr.models.Post
import com.example.humblr.utils.FeedState
import com.example.humblr.utils.PaginationScrollListener
import com.example.humblr.utils.Resource
import com.example.humblr.viewmodels.FeedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedFragment : Fragment() {

    private lateinit var _binding: FragmentFeedBinding
    private val _feedViewModel: FeedViewModel by viewModel()
    private var isLoading = false
    private var _feedState: FeedState = FeedState.NewFeed
    private val _subreddits = mutableListOf<Post>()
    private val _viewPool = RecyclerView.RecycledViewPool()
    private val _feedAdapter by lazy {
        FeedAdapter(
            _onClick = { post ->

                val postFragment = PostFragment.newInstance(post.data.id!!)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_nav_host_fragment, postFragment)
                    .setCustomAnimations(
                        androidx.navigation.ui.R.anim.nav_default_enter_anim,
                        androidx.navigation.ui.R.anim.nav_default_exit_anim
                    )
                    .addToBackStack("post")
                    .commit()
            },
            _voteUp = { post ->
                _feedViewModel.voteUp(post.data.name)
            },
            _voteDown = { post ->
                _feedViewModel.voteDown(post.data.name)
            }
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFeedBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _feedViewModel.getToken()

        _binding.recyclerFeed.adapter = _feedAdapter
        _binding.recyclerFeed.setRecycledViewPool(_viewPool)

//        _feedViewModel.clearToken()
        _feedViewModel.getToken()

        lifecycleScope.launchWhenCreated {
            _feedViewModel.feed.collect {
                when (it) {
                    is Resource.Loading -> {
                        _binding.flProgress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        _binding.flProgress.visibility = View.GONE
                        _binding.flError.visibility = View.GONE
                        isLoading = false
                        _subreddits.addAll(it.data?.data?.children as List<Post>)
                        _feedAdapter.submitList(_subreddits)
                        _feedAdapter.notifyDataSetChanged()
                        paging(it.data.data.after)
                    }
                    is Resource.Error -> {
                        _binding.flProgress.visibility = View.GONE
                        _binding.flError.visibility = View.VISIBLE
                    }
                }
            }
        }

        when (_binding.radioGroup.checkedRadioButtonId) {
            R.id.btn_new -> {
                _feedViewModel.getSubreddits(after = null, "new")
                _feedState = FeedState.NewFeed
                _binding.flProgress.visibility = VISIBLE
            }
            R.id.btn_popular -> {
                _feedViewModel.getSubreddits(after = null, "hot")
                _feedState = FeedState.HotFeed
                _binding.flProgress.visibility = VISIBLE
            }
        }

        _binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btn_new -> {
                    _feedViewModel.getSubreddits(after = null, "new")
                    _feedState = FeedState.NewFeed
                    if (_subreddits.isNotEmpty()) {
                        _subreddits.clear()
                    }
                    _binding.flProgress.visibility = VISIBLE
                }
                R.id.btn_popular -> {
                    _feedViewModel.getSubreddits(after = null, "hot")
                    _feedState = FeedState.HotFeed
                    if (_subreddits.isNotEmpty()) {
                        _subreddits.clear()
                    }
                    _binding.flProgress.visibility = VISIBLE
                }
            }
        }

        _binding.searchView.isSubmitButtonEnabled = true
        _binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (!query.isNullOrEmpty()) {
                    val searchFragment = SearchResultFragment.newInstance(query!!)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main_nav_host_fragment, searchFragment)
                        .addToBackStack("search").commit()

                    _binding.searchView.setQuery(null, false)
                    _binding.searchView.isIconified = true
                    _binding.flProgress.visibility = VISIBLE
                } else {
                    Toast.makeText(requireContext(), R.string.empty_query, Toast.LENGTH_SHORT)
                        .show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun paging(after: String?) {
        _binding.recyclerFeed.addOnScrollListener(object :
            PaginationScrollListener(_binding.recyclerFeed.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                when (_feedState) {
                    is FeedState.NewFeed -> _feedViewModel.getSubreddits(after = after, "new")
                    is FeedState.HotFeed -> _feedViewModel.getSubreddits(after = after, "hot")
                }
                _binding.flProgress.visibility = View.VISIBLE
                isLoading = true
            }

            override fun isLoading() = isLoading
        })
    }
}