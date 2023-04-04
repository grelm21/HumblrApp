package com.example.humblr.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.humblr.R
import com.example.humblr.databinding.FragmentProfileBinding
import com.example.humblr.utils.Resource
import com.example.humblr.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding
    private val _profileViewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launchWhenCreated {
            _profileViewModel.profile.collect{
                when(it){
                    is Resource.Success -> {

                        _binding.profileName.text = it.data!!.subreddit.display_name_prefixed

                        Glide.with(requireContext()).load(it.data!!.subreddit.icon_img)
                            .placeholder(R.drawable.profile_icon_black)
                            .into(_binding.profileImage)

                        _binding.flProgress.visibility = GONE
                    }
                    is Resource.Loading -> {
                        _binding.flProgress.visibility = VISIBLE
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        _binding.logOut.setOnClickListener {
            LogoutDialog()
                .show(childFragmentManager, "Dialog")
        }

        _profileViewModel.getProfile()
    }

}