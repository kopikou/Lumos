package com.example.lumos.presentation.views.fragments.artists

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.lumos.R
import com.example.lumos.databinding.FragmentProfileArtistsBinding
import com.example.lumos.domain.entities.Artist
import com.example.lumos.presentation.dialogs.ProfileEditDialogFragment
import com.example.lumos.presentation.utils.showToast
import com.example.lumos.presentation.viewModels.artists.ProfileArtistViewModel
import com.example.lumos.presentation.viewModels.artists.ProfileArtistViewModelFactory

import com.example.lumos.presentation.views.activities.LoginActivity
import kotlinx.coroutines.launch

class ProfileFragmentArtist : Fragment() {
    private var _binding: FragmentProfileArtistsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileArtistViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ProfileArtistViewModelFactory(requireContext())
        )[ProfileArtistViewModel::class.java]

        setupObservers()
        setupListeners()
        loadArtistData()
    }

    private fun loadArtistData() {
        val firstName = arguments?.getString("firstName") ?: viewModel.getSavedFirstName() ?: return
        val lastName = arguments?.getString("lastName") ?: viewModel.getSavedLastName() ?: return

        viewModel.loadArtist(firstName, lastName)
    }

    private fun setupObservers() {
        viewModel.artist.observe(viewLifecycleOwner) { artist ->
            artist?.let(::updateUI) ?: requireContext().showToast(R.string.artist_data_not_loaded)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { requireContext().showToast(it) }
        }
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener { showEditProfileDialog() }
        binding.btnLogout.setOnClickListener { showLogoutConfirmationDialog() }
    }

    private fun updateUI(artist: Artist) {
        with(binding) {
            tvArtistName.text = getString(R.string.full_name_format, artist.firstName, artist.lastName)
            tvPhone.text = artist.phone
            tvBalance.text = getString(R.string.balance_format, artist.balance)
        }
    }

    private fun showEditProfileDialog() {
        viewModel.artist.value?.let { artist ->
            val dialog = ProfileEditDialogFragment.newInstance(artist)
            dialog.setOnSaveListener { firstName, lastName, phone ->
                viewModel.getUserId()?.let { userId ->
                    launchProfileUpdate(artist.id, userId, firstName, lastName, phone)
                }
            }
            dialog.show(parentFragmentManager, "ProfileEditDialog")
        }
    }

    private fun launchProfileUpdate(
        artistId: Int,
        userId: Int,
        firstName: String,
        lastName: String,
        phone: String
    ) {
        lifecycleScope.launch {
            val success = viewModel.updateProfile(
                artistId = artistId,
                userId = userId,
                firstName = firstName,
                lastName = lastName,
                phone = phone
            )

            if (success) {
                requireContext().showToast(R.string.profile_updated_successfully)
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.logout_title))
            .setMessage(getString(R.string.logout_confirmation))
            .setPositiveButton(getString(android.R.string.yes)) { _, _ ->
                performLogout()
            }
            .setNegativeButton(getString(android.R.string.no), null)
            .show()
    }

    private fun performLogout() {
        viewModel.logout()
        startActivity(Intent(requireActivity(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}