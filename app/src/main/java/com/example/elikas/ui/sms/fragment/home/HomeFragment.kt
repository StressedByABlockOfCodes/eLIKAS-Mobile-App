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
import com.example.elikas.data.User
import com.example.elikas.databinding.FragmentBcHomeBinding
import com.example.elikas.databinding.FragmentCmHomeBinding
import com.example.elikas.utils.SharedPreferenceUtil
import com.example.elikas.utils.SMSUtil

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var mcontext: Context? = null
    private var _binding1: FragmentCmHomeBinding? = null
    private var _binding2: FragmentBcHomeBinding? = null
    private lateinit var user: User
    private lateinit var sms: SMSUtil

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding1 get() = _binding1!!
    private val binding2 get() = _binding2!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        try {
            user = SharedPreferenceUtil.getUser(requireContext())
            when (user.type) {
                "Camp Manager" -> {
                    //switch to CM home fragment
                    _binding1 = FragmentCmHomeBinding.inflate(inflater, container, false)
                    return binding1.root
                }
                "Barangay Captain" -> {
                    //switch to BC home fragment
                    _binding2 = FragmentBcHomeBinding.inflate(inflater, container, false)
                    return binding2.root
                }
                //add for the courier view
                "Courier" -> {
                    //switch to C home fragment
                    return binding1.root
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

        return binding1.root

        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val area = SharedPreferenceUtil.getArea(view.context)
        sms = SMSUtil(view.context)

        when(user.type) {
            "Camp Manager" -> {
                binding1.txtEvacName.text = area.designated_place
                binding1.txtTotalCapacity.text = area.total
                initButtonListenersCM()
            }
            "Barangay Captain" -> {
                binding2.txtBarangayName.text = area.designated_place
                binding2.txtTotalResidents.text = area.total
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
        binding1.dispense.setOnClickListener { subview ->
            subview.findNavController().navigate(R.id.action_navigation_home_to_CMDispenseFragment)
        }
        binding1.request.setOnClickListener { subview ->
            subview.findNavController().navigate(R.id.action_navigation_home_to_CMRequestFragment)
        }
        binding1.viewEvacuees.setOnClickListener { subview ->
            //subview.findNavController().navigate(R.id.action_navigation_home_to_viewSMSFragment)
            val message = "viewEvacuees,${user.id}"
            sms.send(message)
        }
        binding1.viewSupply.setOnClickListener { subview ->
            //subview.findNavController().navigate(R.id.action_navigation_home_to_viewSMSFragment)
            val message = "viewSupply,${user.id}"
            sms.send(message)
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
            //subview.findNavController().navigate(R.id.action_navigation_home_to_viewSMSFragment)
            val message = "viewSupply,${user.id}"
            sms.send(message)
        }
        binding2.viewNonevacuees.setOnClickListener { subview ->
            //subview.findNavController().navigate(R.id.action_navigation_home_to_BCViewNonEvacueesFragment)
            val message = "viewNonEvacuees,${user.id}"
            sms.send(message)
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