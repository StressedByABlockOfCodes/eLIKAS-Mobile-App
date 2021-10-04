package com.example.elikas.ui.sms.fragment.CampManager

import android.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.elikas.databinding.FragmentCmAdmitBinding
import com.example.elikas.databinding.FragmentCmHomeBinding


class CMAdmitFragment : Fragment() {

    private var mcontext: Context? = null
    private var _binding: FragmentCmAdmitBinding? = null
    private lateinit var userType: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCmAdmitBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerViewResidents
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(mcontext)

        //get the data from db and pass to the recyclerview

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
        _binding = null
    }
}