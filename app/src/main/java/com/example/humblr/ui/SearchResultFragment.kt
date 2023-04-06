package com.example.humblr.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.humblr.R
import com.example.humblr.adapters.FeedAdapter
import com.example.humblr.databinding.FragmentSearchResultBinding
import com.example.humblr.models.Post
import com.example.humblr.utils.Resource
import com.example.humblr.viewmodels.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val QUERY = "query"

class SearchResultFragment : Fragment() {

    private lateinit var _binding: FragmentSearchResultBinding

    private val _searchViewModel: SearchViewModel by viewModel()

    private var query: String? = null

    private val _feedAdapter by lazy {
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
                _searchViewModel.voteUp(post.data.name)
            },
            _voteDown = { post ->
                _searchViewModel.voteDown(post.data.name)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            query = it.getString(QUERY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSearchResultBinding.inflate(inflater)

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.recyclerFeed.adapter = _feedAdapter

        _binding.titleTop.text = resources.getString(R.string.search_result, query)

        _binding.backBtn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        lifecycleScope.launchWhenCreated {
            _searchViewModel.search.collect{searchResult ->
                when (searchResult) {
                    is Resource.Loading -> {
                        _binding.flProgress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        if (searchResult.data!!.data.children.isNotEmpty()) {
                            _binding.flError.visibility = View.GONE
                            _feedAdapter.submitList(searchResult.data!!.data.children)
                        }else{
                            _binding.tvError.text = resources.getString(R.string.nothing_found)
                            _binding.flError.visibility = View.VISIBLE
                        }
                        _binding.flProgress.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        _binding.tvError.text = resources.getString(R.string.something_went_wrong)
                        _binding.flProgress.visibility = View.GONE
                        _binding.flError.visibility = View.VISIBLE
                    }
                }
            }
        }

        _searchViewModel.performSearch(query!!)
    }

    companion object {

        @JvmStatic
        fun newInstance(query: String) =
            SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString(QUERY, query)
                }
            }
    }
}