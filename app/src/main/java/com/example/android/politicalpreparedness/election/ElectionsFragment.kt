package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.MainApplication
import com.example.android.politicalpreparedness.data.DataRepository
import com.example.android.politicalpreparedness.data.ElectionDatabase
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.OnClickListener
import com.example.android.politicalpreparedness.network.CivicsApi

class ElectionsFragment : Fragment() {

    companion object {
        private const val TAG = "projectlog"
    }

    private val viewModel by viewModels<ElectionsViewModel> {
        ElectionsViewModelFactory((requireContext().applicationContext as MainApplication).dataRepository)
    }

    private lateinit var binding: FragmentElectionBinding
    private lateinit var electionListAdapter: ElectionListAdapter
    private lateinit var savedListAdapter: ElectionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        electionListAdapter = ElectionListAdapter(OnClickListener { election ->
            findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                    election
                )
            )
        })

        savedListAdapter = ElectionListAdapter(OnClickListener { election ->
            findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                    election
                )
            )
        })

        binding.upcomingElectionsRecyclerview.adapter = electionListAdapter
        binding.savedElectionsRecyclerview.adapter = savedListAdapter

        return binding.root

    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        viewModel.observedElections.observe(viewLifecycleOwner) {
            if (it is Result.Success) {
                Log.i(TAG, "success")
                electionListAdapter.submitList(it.data)
                binding.executePendingBindings()
            } else if (it is Result.Error) {
                Log.i(TAG, "error1")
            } else {
                Log.i(TAG, "error2")
            }
        }

        viewModel.observedFollowedElections.observe(viewLifecycleOwner) {
            if (it is Result.Success) {
                Log.i(TAG, "success")
                savedListAdapter.submitList(it.data)
                binding.executePendingBindings()
            } else if (it is Result.Error) {
                Log.i(TAG, "error1")
            } else {
                Log.i(TAG, "error2")
            }
        }
    }

}