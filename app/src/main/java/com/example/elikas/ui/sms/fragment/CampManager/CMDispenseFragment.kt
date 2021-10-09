package com.example.elikas.ui.sms.fragment.CampManager

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.elikas.MainApplication
import com.example.elikas.databinding.FragmentDispenseBinding
import com.example.elikas.viewmodel.DisasterResponseViewModel
import com.example.elikas.viewmodel.DisasterResponseViewModelFactory
import android.widget.AdapterView
import com.example.elikas.data.DisasterResponse


class CMDispenseFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var mcontext: Context? = null
    private var _binding: FragmentDispenseBinding? = null

    private val viewModel: DisasterResponseViewModel by viewModels {
        DisasterResponseViewModelFactory((requireActivity().application as MainApplication).repositoryDR)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDispenseBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = binding.spinnerDr
        var myList: List<DisasterResponse>? = null


        viewModel.allDisasterResponses.observe(viewLifecycleOwner) { disaster_responses ->
            val adapter = ArrayAdapter(
                view.context,
                android.R.layout.simple_spinner_item,
                disaster_responses
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("Not yet implemented")
        //pass to sms module
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
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