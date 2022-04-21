package com.example.android.politicalpreparedness.representative

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationRequest
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.MainApplication
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.model.Representative
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList
import java.util.Locale

class DetailFragment : Fragment() {

    companion object {
        private const val TAG = "projectlog"
        private const val RECYCLERVIEW_LIST = "recyclerviewList"
        private const val MOTION_LAYOUT_STATE = "motionLayoutState"
    }

    private val viewModel by viewModels<RepresentativeViewModel> {
        RepresentativeViewModelFactory((requireContext().applicationContext as MainApplication).dataRepository)
    }

    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var representativeListAdapter: RepresentativeListAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.]
                Log.i(TAG, "Precise location access granted")
                binding.buttonLocation.callOnClick()
            }
            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                Log.i(TAG, "Only approximate location access granted")
                binding.buttonLocation.callOnClick()
            }
            else -> {
                // No location access granted.
                Log.i(TAG, "No location access granted")
                Snackbar.make(
                    binding.root,
                    R.string.permission_denied_explanation,
                    Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.settings) {
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(
            RECYCLERVIEW_LIST,
            ArrayList<Representative>(representativeListAdapter.currentList)
        )
        outState.putInt(MOTION_LAYOUT_STATE, binding.motionLayout.currentState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // Construct a FusedLocationProviderClient.
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        representativeListAdapter = RepresentativeListAdapter()
        binding.representativeRecyclerview.adapter = representativeListAdapter

        savedInstanceState?.getInt(MOTION_LAYOUT_STATE)?.let {
            binding.motionLayout.transitionToState(it)
            binding.executePendingBindings()
        }

        savedInstanceState?.getParcelableArrayList<Representative>(RECYCLERVIEW_LIST)?.let {
            representativeListAdapter.submitList(it)
            binding.executePendingBindings()
        }

        viewModel.representatives.observe(viewLifecycleOwner) {
            representativeListAdapter.submitList(it)
            binding.executePendingBindings()
        }

        binding.buttonLocation.setOnClickListener {
            if (isPermissionGranted()) {
                Log.i(TAG, "Foreground permission granted")
                getLocation()
            } else {
                Log.i(TAG, "Requesting location permissions")
                locationPermissionRequest.launch(
                    arrayOf(
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            val addressline1 = binding.addressLine1.text.toString()
            val addressline2 = binding.addressLine2.text.toString()
            val city = binding.city.text.toString()
            val zip = binding.zip.text.toString()
            val state = binding.state.selectedItem.toString()
            val fullAddress = Address(addressline1, addressline2, city, zip, state)

            Log.i(TAG, fullAddress.toFormattedString())
            if (addressline1.isBlank() || city.isBlank() || state.isBlank()) {
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.refreshRepFromNetwork(fullAddress)
            }
        }

        return binding.root
    }

    private fun isPermissionGranted(): Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            ACCESS_FINE_LOCATION
                        )) || (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            ACCESS_COARSE_LOCATION
                        ))

        return foregroundLocationApproved
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.getCurrentLocation(
            LocationRequest.QUALITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            Log.i(TAG, location.toString())
            val address = geoCodeLocation(location)
            Log.i(TAG, address.toFormattedString())
            updateAddressBindings(address)
            viewModel.refreshRepFromNetwork(address)
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun updateAddressBindings(address: Address) {
        binding.addressLine1.setText(address.line1)
        binding.addressLine2.setText(address.line2)
        binding.city.setText(address.city)
        binding.zip.setText(address.zip)
        binding.state.setSelection(resources.getStringArray(R.array.states).indexOf(address.state))
    }
}