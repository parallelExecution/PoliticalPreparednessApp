package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.MainApplication
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.ServiceLocator
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    companion object {
        private const val TAG = "projectlog"
    }

    private lateinit var binding: FragmentVoterInfoBinding
    private val args: VoterInfoFragmentArgs by navArgs()

    private val viewModel by viewModels<VoterInfoViewModel> {
        VoterInfoViewModelFactory(
            args.argElection,
            (requireContext().applicationContext as MainApplication).dataRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.currentElection.observe(viewLifecycleOwner) {
            binding.electionName.title = it.name
            binding.electionDate.text = it.electionDay.toString()
            binding.executePendingBindings()
        }

        viewModel.followedStatus.observe(viewLifecycleOwner) { followed ->
            followed?.let {
                if (followed) {
                    binding.followButton.text = getString(R.string.unfollow_election)
                } else {
                    binding.followButton.text = getString(R.string.follow_election)
                }
                binding.executePendingBindings()
            }
        }

        viewModel.voterInfoResponse.observe(viewLifecycleOwner) { response ->
            val address = response?.let {
                it.state?.get(0)?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()
            }
            address?.let {
                Log.i(TAG, address)
                binding.addressGroup.visibility = View.VISIBLE
                binding.address.text = address
                binding.executePendingBindings()
            }
        }

        binding.stateLocations.setOnClickListener {
            viewModel.voterInfoResponse.value?.apply {
                state?.get(0)?.electionAdministrationBody?.electionInfoUrl?.let {
                    loadUrl(it)
                }
            }
        }

        binding.stateBallot.setOnClickListener {
            viewModel.voterInfoResponse.value?.apply {
                state?.get(0)?.electionAdministrationBody?.ballotInfoUrl?.let {
                    loadUrl(it)
                }
            }
        }

        binding.followButton.setOnClickListener {
            viewModel.updateFollowedStatus(!viewModel.followedStatus.value!!)
            viewModel.updateElection()
        }

        return binding.root
    }

    private fun loadUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}