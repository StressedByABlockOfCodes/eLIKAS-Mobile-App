package com.example.elikas.ui.sms.fragment.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.elikas.databinding.FragmentBcAddSupplyBinding
import com.example.elikas.databinding.FragmentBcHomeBinding
import com.example.elikas.databinding.FragmentCmHomeBinding
import com.example.elikas.utils.SharedPreferenceUtil

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var mcontext: Context? = null
    private var _binding1: FragmentCmHomeBinding? = null
    private var _binding2: FragmentBcHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding1 get() = _binding1!!
    private val binding2 get() = _binding2!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        try {
            if(SharedPreferenceUtil.getUserType(requireContext()) == "Camp Manager") {
                _binding1 = FragmentCmHomeBinding.inflate(inflater, container, false)
                val root: View = binding1.root
                return root
            }
            else if(SharedPreferenceUtil.getUserType(requireContext()) == "Barangay Captain") {
                _binding2 = FragmentBcHomeBinding.inflate(inflater, container, false)
                val root: View = binding2.root
                return root
            } //add for the courier view
            else if(SharedPreferenceUtil.getUserType(requireContext()) == "Courier") {
                val root: View = binding1.root
                return root
            }
        }
        catch (e: IllegalStateException) {
            e.printStackTrace()
        }

        val root: View = binding1.root
        return root
        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mcontext = context
    }

    override fun onDetach() {
        super.onDetach()
        mcontext = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding1 = null
        _binding2 = null
    }
}