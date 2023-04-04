package com.example.humblr.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.humblr.databinding.DialogLogoutBinding
import com.example.humblr.viewmodels.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogoutDialog : DialogFragment() {

    private lateinit var _binding: DialogLogoutBinding
    private val _profileViewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogLogoutBinding.inflate(inflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.btnDecline.setOnClickListener {
            dismiss()
        }

        _binding.btnAccept.setOnClickListener {

            _profileViewModel.clearToken()

            val intent = Intent(requireContext(), OnBoardActivity::class.java)
            startActivity(intent)

            dismiss()
        }
    }
}