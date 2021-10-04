package com.example.elikas.ui.sms.fragment.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.elikas.R
import com.example.elikas.databinding.FragmentBcHomeBinding
import com.example.elikas.databinding.FragmentCmHomeBinding
import com.example.elikas.databinding.FragmentProfileBinding
import com.example.elikas.utils.SharedPreferenceUtil


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var mcontext: Context? = null
    private var _binding1: FragmentCmHomeBinding? = null
    private var _binding2: FragmentBcHomeBinding? = null
    private lateinit var userType: String

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
            when( SharedPreferenceUtil.getUserType( requireContext()) ) {
                "Camp Manager" -> {
                    //switch to CM home fragment
                    _binding1 = FragmentCmHomeBinding.inflate(inflater, container, false)
                    val root: View = binding1.root
                    return root
                }
                "Barangay Captain" -> {
                    //switch to BD home fragment
                    _binding2 = FragmentBcHomeBinding.inflate(inflater, container, false)
                    val root: View = binding2.root
                    return root
                }
                //add for the courier view
                "Courier" -> {
                    //switch to C home fragment
                    val root: View = binding1.root
                    return root
                }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when( SharedPreferenceUtil.getUserType(requireContext()) ) {
            "Camp Manager" -> {
                initButtonListenersCM()
            }
            "Barangay Captain" -> {
                initButtonListenersBC()
            } //add for the courier view
            "Courier" -> {

            }
        }
    }

    private fun initButtonListenersCM() {
        binding1.admit.setOnClickListener { subview ->
            subview.findNavController().navigate(R.id.action_navigation_home_to_CMAdmitFragment2)
        }
        binding1.discharge.setOnClickListener { subview ->
            subview.findNavController().navigate(R.id.action_navigation_home_to_CMDischargeFragment)
        }
        binding1.viewEvacuees.setOnClickListener { subview ->
            subview.findNavController().navigate(R.id.action_navigation_home_to_viewSMSFragment)
        }
        binding1.dispense.setOnClickListener { subview ->
            subview.findNavController().navigate(R.id.action_navigation_home_to_CMDispenseFragment)
        }
        binding1.request.setOnClickListener { subview ->
            subview.findNavController().navigate(R.id.action_navigation_home_to_CMRequestFragment)
        }
        binding1.viewSupply.setOnClickListener { subview ->
            subview.findNavController().navigate(R.id.action_navigation_home_to_viewSMSFragment)
        }
    }

    private fun initButtonListenersBC() {
        binding2.admit.setOnClickListener { subview ->
            subview.findNavController().navigate(R.id.action_navigation_home_to_BCAddFragment)
        }
        binding2.dispense.setOnClickListener { subview ->
            subview.findNavController().navigate(R.id.action_navigation_home_to_BCDispenseFragment)
        }
        binding2.viewSupply.setOnClickListener { subview ->
            subview.findNavController().navigate(R.id.action_navigation_home_to_viewSMSFragment)
        }
        binding2.viewNonevacuees.setOnClickListener { subview ->
            subview.findNavController().navigate(R.id.action_navigation_home_to_viewSMSFragment)
        }

    }

    private fun initButtonListenersCR() {

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