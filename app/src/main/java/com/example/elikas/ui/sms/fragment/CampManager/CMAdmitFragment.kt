package com.example.elikas.ui.sms.fragment.CampManager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.elikas.MainApplication
import com.example.elikas.R
import com.example.elikas.adapter.ResidentAdapter
import com.example.elikas.data.User
import com.example.elikas.databinding.FragmentCmAdmitBinding
import com.example.elikas.utils.SMSUtil
import com.example.elikas.utils.SharedPreferenceUtil
import com.example.elikas.viewmodel.ResidentViewModelFactory
import com.example.elikas.viewmodel.ResidentsViewModel


class CMAdmitFragment : Fragment() {

    private var mcontext: Context? = null
    private var _binding: FragmentCmAdmitBinding? = null
    private lateinit var user: User
    private lateinit var sms: SMSUtil

    private val viewModel: ResidentsViewModel by viewModels {
        ResidentViewModelFactory((requireActivity().application as MainApplication).repository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCmAdmitBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = SharedPreferenceUtil.getUser(view.context)
        sms = SMSUtil(view.context)

        val selectedResidents = ArrayList<String>()
        val recyclerView = binding.recyclerViewResidents
        val adapter = ResidentAdapter()
        adapter.setListener(object: ResidentAdapter.OnResidentCheckListener {
            override fun onResidentCheck(resident: String) {
                selectedResidents.add(resident)
            }

            override fun onResidentUncheck(resident: String) {
                selectedResidents.remove(resident)
            }
        })

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(mcontext)

        //get the data from db and pass to the recyclerview
        subscribeUi(adapter)

        binding.btnAdmit.setOnClickListener {
            var message = "admit,${user.id},"
            for(family_code in selectedResidents.indices) {
                viewModel.changeToEvacuee(selectedResidents[family_code])

                message += if(family_code == (selectedResidents.size - 1)) {
                    selectedResidents[family_code]
                } else
                    "${selectedResidents[family_code]},"
            }
            Log.i(TAG, message)
            sms.send(message)
            view.findNavController().navigate(R.id.navigation_home)
        }

        binding.btnCancel.setOnClickListener {
            view.findNavController().navigate(R.id.navigation_home)
        }
    }

    private fun subscribeUi(adapter: ResidentAdapter) {
        viewModel.getNonEvacuees().observe(viewLifecycleOwner) { residents ->
            adapter.submitList(residents)
        }
        //Log.i("getResidents", viewModel.getResidentsByFamCode("eLIKAS-JRKCsR").toString())
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

    companion object {
        const val TAG = "CMAdmitFragment"
    }
}